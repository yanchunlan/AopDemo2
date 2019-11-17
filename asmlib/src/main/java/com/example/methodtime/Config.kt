package com.example.methodtime

import java.io.File
import java.util.*
/**
 * author:  ycl
 * date:  2019/11/17 15:59
 * desc:
 */

class Config {

    val UNNEED_TRACE_CLASS = arrayOf("R.class", "R$", "Manifest", "BuildConfig")

    //插桩配置文件
    var mTraceConfigFile: String? = null

    private val mNeedTracePackageMap: HashSet<String> by lazy { HashSet<String>() }

    private val mWhiteClassMap: HashSet<String> by lazy { HashSet<String>() }

    private val mWhitePackageMap: HashSet<String> by lazy { HashSet<String>() }

    // 插桩代码所在类
    var mBeatClass: String? = null

    // 是否需要打印出所有被插桩的类和方法
    var mIsNeedLogTraceInfo = false

    fun isNeedTraceClass(fileName: String): Boolean {
        var isNeed = true
        if (fileName.endsWith(".class")) {
            for (unTraceCls in UNNEED_TRACE_CLASS) {
                if (fileName.contains(unTraceCls)) {
                    isNeed = false
                    break
                }
            }
        } else {
            isNeed = false
        }
        return isNeed
    }

    fun isConfigTraceClass(className: String): Boolean {

        fun isInNeedTracePackage(): Boolean {
            var isIn = false
            mNeedTracePackageMap.forEach {
                if (className.contains(it)) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        fun isInWhitePackage(): Boolean {
            var isIn = false
            mWhitePackageMap.forEach {
                if (className.contains(it)) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        fun isInWhiteClass(): Boolean {
            var isIn = false
            mWhiteClassMap.forEach {
                if (className == it) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        return if (mNeedTracePackageMap.isEmpty()) {
            !(isInWhitePackage() || isInWhiteClass())
        } else {
            if (isInNeedTracePackage()) {
                !(isInWhitePackage() || isInWhiteClass())
            } else {
                false
            }
        }
    }


    /**
     * 解析插桩配置文件
     */
    fun parseTraceConfigFile() {

        val traceConfigFile = File(mTraceConfigFile)
        if (!traceConfigFile.exists()) {
            System.out.println("trace config file not exist")
        }

        val configStr = Utils.readFileAsString(traceConfigFile.absolutePath)

        val configArray =
                configStr.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (configArray != null) {
            for (i in 0 until configArray.size) {
                var config = configArray[i]
                if (config.isNullOrBlank()) {
                    continue
                }
                if (config.startsWith("#")) {
                    continue
                }
                if (config.startsWith("[")) {
                    continue
                }

                when {
                    config.startsWith("-tracepackage ") -> {
                        config = config.replace("-tracepackage ", "")
                        mNeedTracePackageMap.add(config)
                        System.out.println("tracepackage:$config")
                    }
                    config.startsWith("-keepclass ") -> {
                        config = config.replace("-keepclass ", "")
                        mWhiteClassMap.add(config)
                        System.out.println("keepclass:$config")
                    }
                    config.startsWith("-keeppackage ") -> {
                        config = config.replace("-keeppackage ", "")
                        mWhitePackageMap.add(config)
                        System.out.println("keeppackage:$config")
                    }
                    config.startsWith("-beatclass ") -> {
                        config = config.replace("-beatclass ", "")
                        mBeatClass = config
                        System.out.println("beatclass:$config")
                    }
                    else -> {
                    }
                }
            }

        }
    }


}

