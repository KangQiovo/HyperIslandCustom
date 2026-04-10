package com.kangqi.hIc.log

enum class LogLevel(val label: String, val short: String) {
    DEBUG("DEBUG", "D"),
    INFO("INFO", "I"),
    HOOK("HOOK", "H"),
    ISLAND("ISLAND", "IS"),
    WARN("WARN", "W"),
    ERROR("ERROR", "E")
}

data class LogEntry(
    val index: Int,
    val timestamp: String,
    val tag: String,
    val message: String,
    val level: LogLevel,
    val pid: Int = 0,
    val tid: Int = 0,
    val packageName: String = ""
)
