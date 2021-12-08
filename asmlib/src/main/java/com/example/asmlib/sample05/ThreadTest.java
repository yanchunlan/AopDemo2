package com.example.asmlib.sample05;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author:  yanchunlan
 * date:  2019/12/07 15:34
 * desc:
 */
public class ThreadTest {

  public static void main(String[] args) {
    // MyThreadPoolExecutor.getThreadPool(2);
    Executors.newFixedThreadPool(2);
  }

  static class MyThreadPoolExecutor extends ThreadPoolExecutor {
    public MyThreadPoolExecutor() {
      super(0, 2, 60, TimeUnit.SECONDS, new LinkedBlockingDeque(20));
    }
    public static MyThreadPoolExecutor getThreadPool(int it) {
      return new MyThreadPoolExecutor();
    }
  }
}
