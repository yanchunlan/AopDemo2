package com.example.javassist.agent;

import com.example.javassist.sample01.Base;

import java.lang.instrument.Instrumentation;

/**
 * author:  ycl
 * date:  2019/11/08 18:12
 * desc:
 */
public class TestAgent {

    /**
     * 热加载
     * JVM在运行时会走到TestAgent类中定义的agentmain()方法，而在这个方法中，
     * 我们利用Instrumentation，将指定类的字节码通过定义的类转化器TestTransformer做
     * 了Base类的字节码替换（通过javassist），并完成了类的重新加载
     */
    public static void agentmain(String args, Instrumentation inst) {

        //指定我们自己定义的Transformer，在其中利用Javassist做字节码替换
        inst.addTransformer(new TestTransformer(), true);

        try {
            //重定义类并载入新的字节码
            inst.retransformClasses(Base.class);
            System.out.println("Agent Load success");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
