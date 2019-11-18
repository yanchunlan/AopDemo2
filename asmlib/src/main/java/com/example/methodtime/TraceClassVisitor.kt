package com.example.methodtime

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * author:  ycl
 * date:  2019/11/17 16:58
 * desc:
 */
class TraceClassVisitor(api: Int, cv: ClassVisitor?, var traceConfig: Config) : ClassVisitor(api, cv) {

    private var className: String? = null
    private var isABSClass = false
    private var isBeatClass = false
    private var isConfigTraceClass = false

    override fun visit( version: Int,
                        access: Int,
                        name: String?,
                        signature: String?,
                        superName: String?,
                        interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        System.out.println("traceTime -> TraceClassVisitor visit name: $name")
        this.className = name
        //抽象方法或者接口
        if (access and Opcodes.ACC_ABSTRACT > 0 || access and Opcodes.ACC_INTERFACE > 0) {
            this.isABSClass = true
        }

        //插桩代码所属类
        val resultClassName = name?.replace(".", "/")
        if (resultClassName == traceConfig.mBeatClass) {
            this.isBeatClass = true
        }

        //是否是配置的需要插桩的类
        name?.let {className ->
            isConfigTraceClass = traceConfig.isConfigTraceClass(className)

        }
        val isNotNeedTraceClass = isABSClass || isBeatClass || !isConfigTraceClass
        if (traceConfig.mIsNeedLogTraceInfo && !isNotNeedTraceClass) {
            System.out.println("MethodTraceMan-trace-class: ${className ?: "未知"}")
        }
    }

    override fun visitMethod(
            access: Int,
            name: String?,
            desc: String?,
            signature: String?,
            exceptions: Array<out String>?): MethodVisitor {
        System.out.println("traceTime -> TraceClassVisitor visitMethod name: $name")
        val isConstructor = MethodFilter.isConstructor(name)
        return if (isABSClass || isBeatClass || !isConfigTraceClass || isConstructor) {
            super.visitMethod(access, name, desc, signature, exceptions)
        } else {
            val mv = cv.visitMethod(access, name, desc, signature, exceptions)
            TraceMethodVisitor(api, mv, access, name, desc, className, traceConfig)
        }
    }
}