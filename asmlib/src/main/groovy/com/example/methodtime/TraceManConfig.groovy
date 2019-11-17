package com.example.methodtime

class TraceManConfig {
    String output // 输出目录
    boolean open // 是否开启transform
    String traceConfigFile // 插桩配置文件
    boolean logTraceInfo // 打印插桩出类和方法

    TraceManConfig() {
        open = true
        output = ""
        logTraceInfo = false
    }
}