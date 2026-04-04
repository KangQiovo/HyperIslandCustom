package com.kangqi.hIc.log

import android.content.Context
import android.util.Log
import com.kangqi.hIc.utils.DeviceInfoHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Central log collector that captures:
 * 1. Real-time logcat output filtered for [HIC] tag
 * 2. In-app module log events via [appendModuleLog]
 * 3. LSPosed log files from /data/adb/lspd/log/ via root shell
 *
 * Inspired by HyperCeiler's LogManager + XposedLogLoader architecture.
 */
object LogCollector {

    private const val TAG = "HIC.LogCollector"
    private const val MAX_ENTRIES = 2000
    private const val LOGCAT_TAG_FILTER = "HIC"
    private val LSPD_LOG_DIRS = listOf(
        "/data/adb/lspd/log",
        "/data/adb/lspd/log.old",
        "/data/misc/lspd/log"
    )

    // Module logs — intercepted in-process + logcat capture
    private val _moduleLogs = MutableStateFlow<List<LogEntry>>(emptyList())
    val moduleLogs: StateFlow<List<LogEntry>> = _moduleLogs.asStateFlow()

    // LSPosed logs — read from log files via root
    private val _lsposedLogs = MutableStateFlow<List<LogEntry>>(emptyList())
    val lsposedLogs: StateFlow<List<LogEntry>> = _lsposedLogs.asStateFlow()

    private val moduleBuffer = CopyOnWriteArrayList<LogEntry>()
    private var moduleIndex = 0

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var logcatJob: Job? = null
    private var logcatProcess: Process? = null
    private var initialized = false
    private var appContext: Context? = null

    /**
     * Initialize log collection. Call once from MainActivity.
     */
    fun init(context: Context) {
        if (initialized) return
        initialized = true
        appContext = context.applicationContext

        // Load existing module log file
        scope.launch {
            loadModuleLogFile(context.filesDir)
        }

        // Start logcat capture
        startLogcatCapture()
    }

    /**
     * Append a module-level log entry from app code.
     * Called by [HicLog] methods.
     */
    fun appendModuleLog(level: LogLevel, tag: String, message: String) {
        val entry = LogEntry(
            index = moduleIndex++,
            timestamp = tsFormat.format(Date()),
            tag = tag,
            message = message,
            level = level,
            pid = android.os.Process.myPid(),
            tid = android.os.Process.myTid()
        )
        moduleBuffer.add(entry)
        trimBuffer(moduleBuffer)
        _moduleLogs.value = moduleBuffer.toList()
    }

    /**
     * Start capturing logcat output in real-time for our HIC tag.
     */
    private fun startLogcatCapture() {
        logcatJob?.cancel()
        logcatJob = scope.launch {
            try {
                val process = Runtime.getRuntime().exec(
                    arrayOf("logcat", "-v", "time", "-s",
                        "$LOGCAT_TAG_FILTER:V",
                        "LSPosed-Bridge:V",
                        "XposedBridge:V",
                        "Xposed:V",
                        "EdXposed-Bridge:V"
                    )
                )
                logcatProcess = process

                val reader = BufferedReader(InputStreamReader(process.inputStream), 8192)
                var line: String?
                while (isActive) {
                    line = reader.readLine()
                    if (line == null) break
                    if (line.isBlank()) continue

                    val entry = parseLogcatLine(line)
                    if (entry != null && !isDuplicate(entry)) {
                        moduleBuffer.add(entry)
                        trimBuffer(moduleBuffer)
                        _moduleLogs.value = moduleBuffer.toList()
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Logcat capture stopped: ${e.message}")
            }
        }
    }

    /**
     * Sync LSPosed logs from system log files.
     * Requires root access to read /data/adb/lspd/log/.
     */
    fun syncLsposedLogs() {
        scope.launch {
            val entries = readLsposedLogFiles()
            _lsposedLogs.value = entries
        }
    }

    /**
     * Force refresh both module and LSPosed logs.
     */
    fun refresh(context: Context) {
        scope.launch {
            loadModuleLogFile(context.filesDir)
            syncLsposedLogs()
        }
    }

    fun clearModuleLogs() {
        moduleBuffer.clear()
        moduleIndex = 0
        _moduleLogs.value = emptyList()
    }

    fun destroy() {
        logcatJob?.cancel()
        logcatProcess?.destroy()
        logcatProcess = null
    }

    // ═══════════════ ZIP Export ═══════════════

    /**
     * Export logs as a ZIP file with device info.
     * Returns the ZIP file path, or null on failure.
     *
     * ZIP structure:
     *   device_info.txt         — device/system/framework information
     *   module_logs.txt         — module runtime logs
     *   lspd/log/{name}.log     — raw LSPosed log files (copied via root)
     *   lspd/log.old/{name}.log — old LSPosed log files
     */
    suspend fun exportToZip(context: Context, moduleActive: Boolean = false): File? =
        withContext(Dispatchers.IO) {
            try {
                val exportDir = File(context.cacheDir, "log_export").apply {
                    deleteRecursively()
                    mkdirs()
                }
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val zipFile = File(exportDir, "hic_logs_$timestamp.zip")

                ZipOutputStream(zipFile.outputStream().buffered()).use { zos ->
                    // 1. Device info
                    val deviceInfo = DeviceInfoHelper.build(context, moduleActive)
                    zos.putNextEntry(ZipEntry("device_info.txt"))
                    zos.write(deviceInfo.toByteArray())
                    zos.closeEntry()

                    // 2. Module logs
                    val moduleLogText = buildModuleLogText()
                    zos.putNextEntry(ZipEntry("module_logs.txt"))
                    zos.write(moduleLogText.toByteArray())
                    zos.closeEntry()

                    // 3. LSPosed raw log files via root
                    copyLspdLogsToZip(zos)

                    // 4. App persistent log file
                    val hicLogFile = File(context.filesDir, "hic_logs.txt")
                    if (hicLogFile.exists() && hicLogFile.canRead()) {
                        zos.putNextEntry(ZipEntry("hic_persistent.txt"))
                        hicLogFile.inputStream().copyTo(zos)
                        zos.closeEntry()
                    }
                }

                zipFile
            } catch (e: Exception) {
                Log.e(TAG, "Failed to export ZIP: ${e.message}", e)
                null
            }
        }

    private fun buildModuleLogText(): String {
        val sb = StringBuilder()
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        sb.appendLine("=== HyperIsland Custom Module Logs ===")
        sb.appendLine("Export time: $dateStr")
        sb.appendLine("Entries: ${moduleBuffer.size}")
        sb.appendLine()
        moduleBuffer.forEach { entry ->
            sb.appendLine("[${entry.timestamp}] [${entry.level.short}] ${entry.tag}: ${entry.message}")
            if (entry.pid > 0) sb.appendLine("  PID:${entry.pid} TID:${entry.tid}")
        }
        return sb.toString()
    }

    private fun copyLspdLogsToZip(zos: ZipOutputStream) {
        for (dirPath in LSPD_LOG_DIRS) {
            try {
                val dirName = dirPath.substringAfterLast("/lspd/")
                val listProcess = Runtime.getRuntime().exec(
                    arrayOf("su", "-c", "ls -1 $dirPath/ 2>/dev/null")
                )
                val files = BufferedReader(InputStreamReader(listProcess.inputStream))
                    .readLines()
                    .filter { it.endsWith(".log") }
                    .sorted()
                listProcess.waitFor()

                for (fileName in files) {
                    try {
                        val catProcess = Runtime.getRuntime().exec(
                            arrayOf("su", "-c", "cat $dirPath/$fileName")
                        )
                        zos.putNextEntry(ZipEntry("lspd/$dirName/$fileName"))
                        catProcess.inputStream.copyTo(zos)
                        zos.closeEntry()
                        catProcess.waitFor()
                    } catch (_: Throwable) {}
                }
            } catch (_: Throwable) {}
        }
    }

    // ── Internal: load module log file ──

    private fun loadModuleLogFile(filesDir: File) {
        val file = File(filesDir, "hic_logs.txt")
        if (!file.exists() || !file.canRead()) return
        try {
            val existingTags = moduleBuffer.map { "${it.timestamp}|${it.message}" }.toHashSet()
            file.readLines().forEach { line ->
                if (line.isBlank()) return@forEach
                val entry = parseHicLogLine(line) ?: return@forEach
                val key = "${entry.timestamp}|${entry.message}"
                if (key !in existingTags) {
                    moduleBuffer.add(entry)
                    existingTags.add(key)
                }
            }
            trimBuffer(moduleBuffer)
            _moduleLogs.value = moduleBuffer.toList()
        } catch (_: Throwable) { }
    }

    // ── Internal: logcat line parsing ──

    private fun parseLogcatLine(line: String): LogEntry? {
        if (line.isBlank()) return null

        val timestamp = line.take(18).trim().ifEmpty {
            tsFormat.format(Date())
        }

        val message = line.trim()
        val lower = message.lowercase()

        // Filter: only keep lines related to our module
        val isRelevant = lower.contains("[hic]") || lower.contains("hic_") ||
                lower.contains("kangqi") || lower.contains("hyperisland")
        if (!isRelevant) {
            if (!lower.contains("lsposed") && !lower.contains("xposed")) return null
        }

        val level = when {
            line.contains(" E/") || line.contains(" E ") || lower.contains("error") -> LogLevel.ERROR
            line.contains(" W/") || line.contains(" W ") || lower.contains("warn") -> LogLevel.WARN
            line.contains(" D/") || line.contains(" D ") -> LogLevel.DEBUG
            lower.contains("hook") || lower.contains("intercept") -> LogLevel.HOOK
            lower.contains("island") || lower.contains("posted") || lower.contains("trigger") -> LogLevel.ISLAND
            else -> LogLevel.INFO
        }

        val hicMatch = Regex("""\[HIC]\s*(.*)""").find(message)
        val body = hicMatch?.groupValues?.get(1)?.trim() ?: message
        val tag = when {
            body.contains(":") -> body.substringBefore(":").trim().take(30)
            else -> level.label
        }
        val msgBody = if (body.contains(":")) body.substringAfter(":").trim() else body

        return LogEntry(
            index = moduleIndex++,
            timestamp = timestamp,
            tag = tag,
            message = msgBody,
            level = level
        )
    }

    // ── Internal: read LSPosed log files via root ──

    private suspend fun readLsposedLogFiles(): List<LogEntry> = withContext(Dispatchers.IO) {
        val entries = mutableListOf<LogEntry>()
        var idx = 0

        for (dir in LSPD_LOG_DIRS) {
            try {
                val listProcess = Runtime.getRuntime().exec(
                    arrayOf("su", "-c", "ls -1 $dir/ 2>/dev/null")
                )
                val allFiles = BufferedReader(InputStreamReader(listProcess.inputStream))
                    .readLines()
                    .filter { it.endsWith(".log") }
                    .sorted()
                listProcess.waitFor()

                if (allFiles.isEmpty()) continue

                // Prefer modules_*.log files (like HyperCeiler), fallback to verbose_*.log, then any .log
                val moduleFiles = allFiles.filter { it.startsWith("modules_") }
                val verboseFiles = allFiles.filter { it.startsWith("verbose_") }
                val filesToRead = moduleFiles.ifEmpty { verboseFiles }.ifEmpty { allFiles }

                for (fileName in filesToRead) {
                    try {
                        val catProcess = Runtime.getRuntime().exec(
                            arrayOf("su", "-c", "cat $dir/$fileName")
                        )
                        val reader = BufferedReader(InputStreamReader(catProcess.inputStream), 16384)
                        val currentEntry = StringBuilder()
                        var currentTimestamp = ""
                        var currentLevel = ""
                        var currentUid = 0
                        var currentPid = 0
                        var currentTid = 0

                        reader.forEachLine { line ->
                            // Try full LSPosed format first:
                            // [  2024-01-15T12:34:56.789  10123:  1234:  5678 I/tag] message
                            val fullMatch = LSPD_FULL_LINE.find(line)
                            // Fallback: simpler format [  2024-01-15T12:34:56.789 I ...
                            val simpleMatch = if (fullMatch == null) LSPD_SIMPLE_LINE.find(line) else null

                            val matchResult = fullMatch ?: simpleMatch

                            if (matchResult != null) {
                                // Flush previous entry
                                if (currentEntry.isNotEmpty()) {
                                    val entry = buildLsposedEntry(
                                        idx++, currentTimestamp, currentLevel,
                                        currentEntry.toString(), fileName,
                                        currentPid, currentTid
                                    )
                                    if (entry != null) entries.add(entry)
                                    currentEntry.clear()
                                }

                                if (fullMatch != null) {
                                    currentTimestamp = fullMatch.groupValues[1]
                                    currentUid = fullMatch.groupValues[2].trim().toIntOrNull() ?: 0
                                    currentPid = fullMatch.groupValues[3].trim().toIntOrNull() ?: 0
                                    currentTid = fullMatch.groupValues[4].trim().toIntOrNull() ?: 0
                                    currentLevel = fullMatch.groupValues[5]
                                } else if (simpleMatch != null) {
                                    currentTimestamp = simpleMatch.groupValues[1]
                                    currentLevel = simpleMatch.groupValues.getOrElse(2) { "I" }
                                    currentPid = 0
                                    currentTid = 0
                                }
                                val msgPart = line.substring(matchResult.range.last + 1).trim()
                                    .trimStart(']').trim()
                                currentEntry.append(msgPart)
                            } else if (currentEntry.isNotEmpty()) {
                                // Continuation line
                                if (currentEntry.length < 32768) {
                                    currentEntry.append("\n").append(line)
                                }
                            }
                        }
                        // Flush last entry
                        if (currentEntry.isNotEmpty()) {
                            val entry = buildLsposedEntry(
                                idx++, currentTimestamp, currentLevel,
                                currentEntry.toString(), fileName,
                                currentPid, currentTid
                            )
                            if (entry != null) entries.add(entry)
                        }

                        catProcess.waitFor()
                    } catch (_: Throwable) { }
                }

                if (entries.isNotEmpty()) break // Got data from this dir
            } catch (_: Throwable) { }
        }

        // Also try reading without root (some ROMs allow)
        if (entries.isEmpty()) {
            entries.addAll(readLsposedLogsWithoutRoot(idx))
        }

        entries.sortedByDescending { it.timestamp }.take(MAX_ENTRIES)
    }

    private fun readLsposedLogsWithoutRoot(startIdx: Int): List<LogEntry> {
        val entries = mutableListOf<LogEntry>()
        var idx = startIdx
        val paths = listOf(
            "/data/adb/lspd/log/modules.log",
            "/data/misc/lspd/log/modules.log"
        )
        for (path in paths) {
            try {
                val file = File(path)
                if (!file.exists() || !file.canRead()) continue
                file.readLines().takeLast(1000).forEach { line ->
                    if (line.isBlank()) return@forEach
                    val entry = parseFlatLsposedLine(line, idx++)
                    if (entry != null) entries.add(entry)
                }
                break
            } catch (_: Throwable) { }
        }
        return entries
    }

    private fun buildLsposedEntry(
        index: Int, timestamp: String, level: String,
        message: String, sourceFile: String,
        pid: Int = 0, tid: Int = 0
    ): LogEntry? {
        if (message.isBlank()) return null
        val logLevel = when (level.uppercase().firstOrNull()) {
            'E' -> LogLevel.ERROR
            'W' -> LogLevel.WARN
            'D' -> LogLevel.DEBUG
            'V' -> LogLevel.DEBUG
            'C' -> LogLevel.ERROR
            else -> LogLevel.INFO
        }
        // Extract tag: look for module bracket pattern [package,ModuleName] or tag:
        val bracketMatch = LSPD_MODULE_TAG.find(message)
        val tag = when {
            bracketMatch != null -> bracketMatch.groupValues[2].ifBlank { bracketMatch.groupValues[1] }
            message.contains(":") -> message.substringBefore(":").trim().take(40)
            else -> sourceFile
        }
        val body = when {
            bracketMatch != null -> message.substring(bracketMatch.range.last + 1).trim()
            message.contains(":") -> message.substringAfter(":").trim()
            else -> message
        }

        return LogEntry(
            index = index,
            timestamp = timestamp,
            tag = tag,
            message = body,
            level = logLevel,
            pid = pid,
            tid = tid
        )
    }

    private fun parseFlatLsposedLine(line: String, index: Int): LogEntry? {
        if (line.isBlank()) return null
        val tsMatch = Regex("""(\d{2,4}[-/]\d{2}[-/]?\d{0,2}\s+\d{2}:\d{2}:\d{2})""").find(line)
        val timestamp = tsMatch?.value ?: tsFormat.format(Date())
        val message = line.trim()
        val lower = message.lowercase()
        val level = when {
            lower.contains("error") || lower.contains(" e ") -> LogLevel.ERROR
            lower.contains("warn") || lower.contains(" w ") -> LogLevel.WARN
            lower.contains("hook") || lower.contains("xposed") -> LogLevel.HOOK
            else -> LogLevel.INFO
        }
        val tag = if (message.contains(":")) message.substringBefore(":").trim().take(40) else "LSPosed"
        val body = if (message.contains(":")) message.substringAfter(":").trim() else message
        return LogEntry(index = index, timestamp = timestamp, tag = tag, message = body, level = level)
    }

    private fun parseHicLogLine(line: String): LogEntry? {
        if (line.isBlank()) return null
        val tsMatch = Regex("""(\d{2,4}[-/]\d{2}[-/]?\d{0,2}\s+\d{2}:\d{2}:\d{2})""").find(line)
        val timestamp = tsMatch?.value ?: tsFormat.format(Date())
        val hicMatch = Regex("""\[HIC]\s*(.*)""").find(line)
        val message = hicMatch?.groupValues?.get(1)?.trim() ?: line.trim()
        val lower = message.lowercase()
        val level = when {
            lower.contains("error") || lower.contains("fail") || lower.contains("exception") -> LogLevel.ERROR
            lower.contains("hook") || lower.contains("intercept") -> LogLevel.HOOK
            lower.contains("island") || lower.contains("posted") || lower.contains("trigger") ||
                    lower.contains("receiver") || lower.contains("dismiss") -> LogLevel.ISLAND
            else -> LogLevel.INFO
        }
        val tag = if (message.contains(":")) message.substringBefore(":").trim().take(30) else level.label
        val body = if (message.contains(":")) message.substringAfter(":").trim() else message
        return LogEntry(index = moduleIndex++, timestamp = timestamp, tag = tag, message = body, level = level)
    }

    private fun isDuplicate(entry: LogEntry): Boolean {
        if (moduleBuffer.isEmpty()) return false
        val last = moduleBuffer.lastOrNull() ?: return false
        return last.timestamp == entry.timestamp && last.message == entry.message
    }

    private fun trimBuffer(buffer: CopyOnWriteArrayList<LogEntry>) {
        while (buffer.size > MAX_ENTRIES) {
            buffer.removeAt(0)
        }
    }

    private val tsFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())

    // LSPosed log format (full):
    // [  2024-01-15T12:34:56.789  10123:  1234:  5678 I/tag] message
    private val LSPD_FULL_LINE = Regex(
        """\[\s*(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3})\s+(\d+):\s*(\d+):\s*(\d+)\s+([VDIWEC])/"""
    )

    // LSPosed log format (simple fallback):
    // [  2024-01-15T12:34:56.789 I  ...
    private val LSPD_SIMPLE_LINE = Regex(
        """\[\s*(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3})\s+([VDIWEC])\s"""
    )

    // Module bracket tag: [package.name,ModuleName]
    private val LSPD_MODULE_TAG = Regex("""\[([^,\]]+),([^\]]+)\]""")
}
