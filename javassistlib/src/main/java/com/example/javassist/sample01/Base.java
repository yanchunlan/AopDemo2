package com.example.javassist.sample01;

import java.lang.management.ManagementFactory;

public class Base {
    public static void process(){
        System.out.println("process");
    }

    /**
     * 解决第一种 JavassistTest ，如果在classLoader加载之后就不能动态修改代码了，
     */
    public static void main(String[] args) {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("name: "+name);

        String pid = name.split("@")[0];
        System.out.println("pid: "+pid);
        while (true) {
            try {
                Thread.sleep(5000L);
            } catch (Exception e) {
                break;
            }
            process();
        }
    }
}