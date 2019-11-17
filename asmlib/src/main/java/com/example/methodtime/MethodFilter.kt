package com.example.methodtime

/**
 * author:  ycl
 * date:  2019/11/17 19:24
 * desc:
 */
class MethodFilter {

    companion object {

        fun isConstructor(methodName: String?): Boolean {
            return methodName?.contains("<init>") ?: false
        }

    }
}