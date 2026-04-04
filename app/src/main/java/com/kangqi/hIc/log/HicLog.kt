package com.kangqi.hIc.log

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized logging utility for HyperIsland Custom.
 *
 * Every call writes to:
 * 1. Android logcat (always)
 * 2. LogCollector in-memory buffer (for real-time UI display)
 * 3. Persistent log file (hic_logs.txt) for survival across restarts
 *
 * Inspired by HyperCeiler's AndroidLog + LogListener pattern.
 */
object HicLog {

    private const val TAG = "HIC"
    private var logFile: File? = null

    fun init(filesDir: File) {
        logFile = File(filesDir, "hic_logs.txt")
        // Trim log file if too large (> 1MB)
        logFile?.let { f ->
            if (f.exists() && f.length() > 1_048_576) {
                try {
                    val lines = f.readLines()
                    f.writeText(lines.takeLast(2000).joinToString("\n"))
                } catch (_: Throwable) { }
            }
        }
    }

    fun d(tag: String, message: String) {
        Log.d(TAG, "[$tag] $message")
        LogCollector.appendModuleLog(LogLevel.DEBUG, tag, message)
        writeToFile("D", tag, message)
    }

    fun i(tag: String, message: String) {
        Log.i(TAG, "[$tag] $message")
        LogCollector.appendModuleLog(LogLevel.INFO, tag, message)
        writeToFile("I", tag, message)
    }

    fun w(tag: String, message: String) {
        Log.w(TAG, "[$tag] $message")
        LogCollector.appendModuleLog(LogLevel.WARN, tag, message)
        writeToFile("W", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, "[$tag] $message", throwable)
        } else {
            Log.e(TAG, "[$tag] $message")
        }
        val fullMsg = if (throwable != null) "$message\n${throwable.stackTraceToString()}" else message
        LogCollector.appendModuleLog(LogLevel.ERROR, tag, fullMsg)
        writeToFile("E", tag, fullMsg)
    }

    fun hook(tag: String, message: String) {
        Log.i(TAG, "[Hook:$tag] $message")
        LogCollector.appendModuleLog(LogLevel.HOOK, tag, message)
        writeToFile("H", tag, message)
    }

    fun island(tag: String, message: String) {
        Log.i(TAG, "[Island:$tag] $message")
        LogCollector.appendModuleLog(LogLevel.ISLAND, tag, message)
        writeToFile("IS", tag, message)
    }

    private fun writeToFile(level: String, tag: String, message: String) {
        val file = logFile ?: return
        try {
            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val pid = android.os.Process.myPid()
            val tid = android.os.Process.myTid()
            val line = "[$ts] [$level] $pid:$tid [$TAG:$tag] $message\n"
            FileOutputStream(file, true).use { it.write(line.toByteArray()) }
        } catch (_: Throwable) { }
    }
}
