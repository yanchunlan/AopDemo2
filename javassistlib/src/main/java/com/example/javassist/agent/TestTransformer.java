package com.example.javassist.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * author:  ycl
 * date:  2019/11/08 18:06
 * desc:
 */
public class TestTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("loader: "+ loader+" className: "+ className+" classBeingRedefined: "+ classBeingRedefined+" protectionDomain: "+ protectionDomain+" classfileBuffer: "+classfileBuffer);


        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get("com.example.javassist.sample01.Base");
            CtMethod ctMethod = ctClass.getDeclaredMethod("process");

            ctMethod.insertBefore("{ System.out.println(\"start\"); }");
            ctMethod.insertAfter("{ System.out.println(\"end\"); }");
            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
