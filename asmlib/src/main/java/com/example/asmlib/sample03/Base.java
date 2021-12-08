package com.example.asmlib.sample03;

public class Base {

    /**
     * 1.   替换内部类 Thread 为 myThread
     */
    public void test() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Base03").start();
    }

    //    针对1问题，检测是继承了线程的是否有影响
    public void test1() {
        AThread aThread = new AThread();
        BThread bThread = new BThread();
    }

    class AThread extends Thread {

    }

    class BThread extends AThread {
        public String returnString() {
            return null;
        }
    }

    /**
     * 2.   判断哪里调用了类的方法
     */
    public String getIMEI() {
        return new BThread().returnString();
    }
}