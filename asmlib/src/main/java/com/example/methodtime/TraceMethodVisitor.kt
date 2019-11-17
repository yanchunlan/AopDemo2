package com.example.methodtime

import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter

/**
 * author:  ycl
 * date:  2019/11/17 19:27
 * desc:
 */
class TraceMethodVisitor(
        api: Int,
        mv: MethodVisitor?,
        access: Int,
        name: String?,
        desc: String?,
        className: String?,
        var traceConfig: Config
) : AdviceAdapter(api, mv, access, name, desc) {

    private var methodName: String? = null
    private var name: String? = null
    private var className: String? = null
    private val maxSectionNameLength = 127

    init {
        val traceMethod = TraceMethod.create(0, access, className, name, desc)
        this.methodName = traceMethod.getMethodNameText()
        this.className = className
        this.name = name
    }


    override fun onMethodEnter() {
        super.onMethodEnter()
        val methodName = generatorMethodName()

        mv.visitLdcInsn(methodName)
        mv.visitMethodInsn(
                INVOKESTATIC,
                traceConfig.mBeatClass,
                "start",
                "(Ljava/lang/String;)V",
                false
        )

        if (traceConfig.mIsNeedLogTraceInfo) {
            System.out.println("MethodTraceMan-trace-method: ${methodName ?: "未知"}")
        }
    }

    override fun onMethodExit(p0: Int) {
        super.onMethodExit(p0)
        val methodName = generatorMethodName()

        mv.visitLdcInsn(methodName)
        mv.visitMethodInsn(
                INVOKESTATIC,
                traceConfig.mBeatClass,
                "end",
                "(Ljava/lang/String;)V",
                false
        )
    }


    private fun generatorMethodName(): String? {
        var sectionName = methodName
        var length = sectionName?.length ?: 0
        if (length > maxSectionNameLength && !sectionName.isNullOrBlank()) {
            // 先去掉参数
            val parmIndex = sectionName.indexOf('(')
            sectionName = sectionName.substring(0, parmIndex)
            // 如果依然更大，直接裁剪
            length = sectionName.length
            if (length > 127) {
                sectionName = sectionName.substring(length - maxSectionNameLength)
            }
        }
        return sectionName
    }
}