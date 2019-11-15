package com.example.methodtime

class TraceManConfig {
    String output
    boolean open
    String traceConfigFile
    boolean logTraceInfo

    TraceManConfig() {
        open = true
        output = ""
        logTraceInfo = false
    }
}