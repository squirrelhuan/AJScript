package cn.demomaster.ajscript.engine.j2v8;

import com.eclipsesource.v8.V8;

public class MyThread extends Thread{

    MyThread(Runnable runnable){
        super(runnable);
    }
}
