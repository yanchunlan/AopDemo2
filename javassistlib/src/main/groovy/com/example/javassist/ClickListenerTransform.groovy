package com.example.javassist

import com.android.SdkConstants
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class ClickListenerTransform extends Transform {

    Project project
    TransformOutputProvider outputProvider
    AppExtension appExtension

    ClassPool pool = ClassPool.getDefault()
    def CLICK_LISTENER = "android.view.View\$OnClickListener"

    ClickListenerTransform(Project project, AppExtension appExtension) {
        this.project = project
        this.appExtension = appExtension
    }

    @Override
    String getName() {
        return ClickListenerTransform.class.getSimpleName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation invocation) throws TransformException, InterruptedException, IOException {
        super.transform(invocation)
        outputProvider = invocation.outputProvider

        appExtension.bootClasspath.each {
            System.out.println("bootClasspath it.absolutePath " + it.absolutePath)
            pool.appendClassPath(it.absolutePath)
        }

        invocation.inputs.each {
            traversalDirInputs(it)
            traversalJarInputs(it)
        }
    }

    private void traversalDirInputs(TransformInput input) {
        input.directoryInputs.each {
            String fileName = it.file.absolutePath
            System.out.println("traversalDirInputs copy fileName " + fileName)
            pool.insertClassPath(fileName)
            findTarget(it.file, fileName)

            def dest = outputProvider.getContentLocation(it.name
                    , it.contentTypes, it.scopes, Format.DIRECTORY)
            System.out.println("traversalDirInputs dest fileName " + dest)
            FileUtils.copyDirectory(it.file, dest)
        }
    }

    private void findTarget(File dir, String fileName) {
        if (dir.isDirectory()) {
            dir.listFiles().each {
                findTarget(it, fileName)
            }
        } else {
            modify(dir, fileName)
        }
    }

    private void modify(File dir, String fileName) {
        String filePath = dir.absolutePath
        System.out.println("modify filePath " + filePath)
        return
        if (isClassFile(filePath)) {
            String className = filePath.replace(fileName, "")
                    .replace("/", ".")
                    .replace("\\", ".")
            def name = className.replace(SdkConstants.DOT_CLASS, "").substring(1)
            /*  def end = name.indexOf("\$")
              if (end != -1) {
                  name=name.substring(0,end)
              }*/

            CtClass ctClass = pool.get(name)
            System.out.println("modify ctClass.name " + ctClass.name)
            CtClass[] interfaces = ctClass.getInterfaces()
            if (interfaces.contains(pool.get(CLICK_LISTENER))) {
                if (name.contains("\$")) {
                    System.out.println("modify contains ctClass.name " + ctClass.name)
                    System.out.println("modify ctClass " + ctClass)
                    CtClass outer = pool.get(name.substring(0, name.indexOf("\$")))
                    CtField ctField = ctClass.getField().find {
                        return it.type == outer
                    }
                    if (ctField != null) {
                        System.out.println("modify ctField.Name " + ctField.name)

                        def body = "android.widget.Toast.makeText(\$1.getContext(),\"toast\",android.widget.Toast.LENGTH_SHORT).show();"
                        addCode(ctClass, body, fileName)
                    }
                }
            } else {
                def body = "android.widget.Toast.makeText(\$1.getContext(),\"toast\",android.widget.Toast.LENGTH_SHORT).show();"
                addCode(ctClass, body, fileName)
            }
        }
    }

    private boolean addCode(CtClass ctClass, String body, String fileName) {
        if (c.isFrozen()) {
            c.defrost()
        }
        CtMethod ctMethod = ctClass.getDeclaredMethod("onClick", pool.get("android.view.View"))
        ctMethod.insertAfter(body)

        ctClass.writeFile(fileName)
        ctClass.detach()

        System.out.println("addCode ctField.Name " + ctField.name)
        System.out.println("addCode ctMethod.Name " + ctMethod.name)

    }

    private boolean isClassFile(String filePath) {
        return filePath.endsWith(".class") &&
                !filePath.contains('R$') &&
                !filePath.contains('R.class') &&
                !filePath.contains("BuildConfig.class")
    }

    private void traversalJarInputs(TransformInput inputs) {
        inputs.jarInputs.each {
            JarInput jarInput ->

                pool.insertClassPath(jarInput.file.absolutePath)

                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
//                System.out.println("traversalJarInputs copy jarName " + jarName)
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
//                System.out.println("traversalJarInputs dest fileName " + dest)
                FileUtils.copyFile(jarInput.file, dest)
        }
    }
}