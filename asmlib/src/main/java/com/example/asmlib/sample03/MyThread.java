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
    }

    public MyThread(Runnable target) {
        super(target);
    }

    public MyThread(@Nullable ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public MyThread(@NotNull String name) {
        super(name);
    }

    public MyThread(@Nullable ThreadGroup group, @NotNull String name) {
        super(group, name);
    }

    public MyThread(Runnable target, String name) {
        super(target, name);
    }

    public MyThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name) {
        super(group, target, name);
    }

    public MyThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name, long stackSize) {
        super(group, target, name, stackSize);
    }
}
