package com.example.asmlib.sample03;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * author:  ycl
 * date:  2019/12/12 14:28
 * desc:
 */
public class MyThread extends Thread {
    public MyThread() {
        System.out.println("MyThread");
    }

    public MyThread(Runnable target) {
        super(target);
        System.out.println("MyThread");
    }

    public MyThread(@Nullable ThreadGroup group, Runnable target) {
        super(group, target);
        System.out.println("MyThread");
    }

    public MyThread(@NotNull String name) {
        super(name);
        System.out.println("MyThread");
    }

    public MyThread(@Nullable ThreadGroup group, @NotNull String name) {
        super(group, name);
        System.out.println("MyThread");
    }

    public MyThread(Runnable target, String name) {
        super(target, name);
        System.out.println("MyThread");
    }

    public MyThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name) {
        super(group, target, name);
        System.out.println("MyThread");
    }

    public MyThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name, long stackSize) {
        super(group, target, name, stackSize);
        System.out.println("MyThread");
    }
}
