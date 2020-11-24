package com.example.methodtime

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import com.google.common.collect.Sets

class TraceManTransform extends Transform {
    interface ScopesType {
        int TYPE_APP = 1
        int TYPE_LIB = 2
    }
    public static final Set<QualifiedContent.Scope> SCOPE_FULL_LIB = Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT)

    private Project project
    private int scopesType

    public TraceManTransform(Project project, int scopesType) {
        this.project = project
        this.scopesType = scopesType
    }

    @Override
    String getName() {
        return TraceManTransform.class.getSimpleName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        if (scopesType == ScopesType.TYPE_LIB) {
            return SCOPE_FULL_LIB
        }
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        System.out.println("traceTime -> transform start")
        long startTime = System.currentTimeMillis()

        def traceManConfig = project.traceMan
        String output = traceManConfig.output
        if (output == null || output.isEmpty()) {
            traceManConfig.output = project.getBuildDir().getAbsolutePath() + File.separator + "traceman_output"
        }

        if (!traceManConfig.open) {
            defaultEach(transformInvocation)
            return
        }

        Config traceConfig = initConfig(traceManConfig) // 初始化配置文件
         // 解析配置文件
        if (!traceConfig.parseTraceConfigFile()) {
            defaultEach(transformInvocation)
            return
        }

        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }

        Collection<TransformInput> inputs = transformInvocation.inputs
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                traceSrcFiles(directoryInput, outputProvider, traceConfig)
            }
            input.jarInputs.each { JarInput jarInput ->
                traceJarFiles(jarInput, outputProvider, traceConfig)
            }
        }

        long endTime = System.currentTimeMillis()
        System.out.println("traceTime -> transform end cost: ${endTime-startTime}")
    }

    // 默认什么都不执行
    private void defaultEach(TransformInvocation transformInvocation) {

        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }

        Collection<TransformInput> inputs = transformInvocation.inputs
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                defaultSrcFiles(directoryInput, outputProvider)
            }
            input.jarInputs.each { JarInput jarInput ->
                defaultJarFiles(jarInput, outputProvider)
            }
        }
    }

    Config initConfig(TraceManConfig traceManConfig) {
        Config config = new Config()
        config.MTraceConfigFile = traceManConfig.traceConfigFile
        config.MIsNeedLogTraceInfo = traceManConfig.logTraceInfo
        return config
    }

    void traceSrcFiles(DirectoryInput directoryInput, TransformOutputProvider outputProvider, Config traceConfig) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
//                System.out.println("traceTime -> traceSrcFiles name: ${name}")

                if (traceConfig.isNeedTraceClass(name)) {
                    System.out.println("traceTime -> traceSrcFiles startAsm")

                    // asm 操作
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new TraceClassVisitor(Opcodes.ASM5, classWriter, traceConfig)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)

                    // 更改之后，替换当前文件
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()

                    System.out.println("traceTime -> traceSrcFiles endAsm")
                }
            }
        }
        //处理完输出给下一任务作为输入
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)

    }

    // 流程： src - >  temp 文件 -> dest
    void traceJarFiles(JarInput jarInput, TransformOutputProvider outputProvider, Config traceConfig) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {

            def jarName = jarInput.name
            System.out.println("traceTime -> traceJarFiles file.getAbsolutePath: ${jarInput.file.getAbsolutePath()}")
            System.out.println("traceTime -> traceJarFiles jarName: ${jarInput.name}")

            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }

            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()

            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))

            // 循环jar包里面的文件
            while (enumeration.hasMoreElements()) {

                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                System.out.println("traceTime -> traceJarFiles entryName: ${entryName}")
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (traceConfig.isNeedTraceClass(entryName)) {
                    jarOutputStream.putNextEntry(zipEntry)

                    // asm 操作
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new TraceClassVisitor(Opcodes.ASM5, classWriter, traceConfig)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()

                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }

            jarOutputStream.close()
            jarFile.close()

            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)

            tmpFile.delete()
        }
    }

    void defaultSrcFiles(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    void defaultJarFiles(JarInput jarInput, TransformOutputProvider outputProvider) {
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        def dest = outputProvider.getContentLocation(jarName + "_" + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        FileUtils.copyFile(jarInput.file, dest)
    }

}