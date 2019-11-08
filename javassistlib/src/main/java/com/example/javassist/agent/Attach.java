package com.example.javassist.agent;

/**
 * author:  ycl
 * date:  2019/11/08 19:14
 * desc:
 */
public class Attach {

    /**
     * 应用范围：
     *     热部署：不部署服务而对线上服务做修改，可以做打点、增加日志等操作。
     *     Mock：测试时候对某些服务做Mock。
     *     性能诊断工具：比如bTrace就是利用Instrument，实现无侵入地跟踪一个正在运行的JVM，监控到类和方法级别的状态信息
     */
    public static void main(String[] args) {
        // 传入目标 JVM pid
     /*   VirtualMachine vm = VirtualMachine.attach("39333");
        vm.loadAgent("/javassistlib/build/classes/java/main/test/operation_server_jar/operation-server.jar");
   */
    }
}
