package com.example.javassist.sample01;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * author:  ycl
 * date:  2019/11/08 16:38
 * desc:
 *      其中只包含一个process()方法，方法内输出一行“process”。
 *      修改后，方法执行前输出“start”，之后输出"end"
 *      System.out.println("start");
 *      System.out.println("end");
 */
public class JavassistTest {
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, IllegalAccessException, InstantiationException {

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.example.javassist.sample01.Base");
        CtMethod ctMethod = ctClass.getDeclaredMethod("process");

        ctMethod.insertBefore("{ System.out.println(\"start\"); }");
        ctMethod.insertAfter("{ System.out.println(\"end\"); }");

        Class c = ctClass.toClass();
        ctClass.writeFile("javassistlib/build/classes/java/main/test");

        ctClass.detach();
        Base h = (Base) c.newInstance();
        h.process();
    }
}
