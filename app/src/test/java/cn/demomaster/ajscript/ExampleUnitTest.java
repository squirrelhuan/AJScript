package cn.demomaster.ajscript;

import com.alibaba.fastjson.JSON;
import com.eclipsesource.v8.V8;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import cn.demomaster.ajscript.test.BlockQueue;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void jsonStr() {
        String a="[1,2,3]";
        Object a1 = JSON.parseArray(a);
        String b="[1,2,3,\"4\"]";
        Object b1 = JSON.parseArray(b);
        String c="[\"a\",\"b\",\"c\",\"d\"]";
        Object c1 = JSON.parseArray(c);
        System.out.println(a1+""+b1+c1);
    }

    @Test
    public void ajs() {
        System.out.println("**********************=");
        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntegerScript(""
                + "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world).length;\n");
        System.out.println("result="+result);
        runtime.release();
    }

    @Test
    public void main() {
        BlockQueue<Integer> queue = new BlockQueue<Integer>(4);
        try {
            queue.put(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                System.out.println("添加");
                queue.put(11);
                queue.put(12);
                queue.put(13);
                queue.put(14);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                System.out.println("取出");
                queue.take();
                Thread.sleep(1);
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

       /* try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    final static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
    @Test
    public void main1() throws InterruptedException {
        CountDownLatch latch= new CountDownLatch(2);//两个工人的协作
        Worker worker1= new Worker("zhang san" , 5000, latch);
        Worker worker2= new Worker("li si" , 8000, latch);
        worker1.start(); //
        worker2.start(); //
        latch.await(); //等待所有工人完成工作
        System. out.println("all work done at " +sdf.format(new Date())+" "+Thread.currentThread().getName());
    }


    static class Worker extends Thread{
        String workerName;
        int workTime ;
        CountDownLatch latch;
        public Worker(String workerName ,int workTime ,CountDownLatch latch){
            this.workerName =workerName;
            this.workTime =workTime;
            this.latch =latch;
        }
        public void run(){
            System. out.println("Worker " +workerName +" do work begin at "+sdf.format( new Date()));
            doWork(); //工作了
            System. out.println("Worker " +workerName +" do work complete at "+sdf.format( new Date()));
            latch.countDown();//工人完成工作，计数器减一

        }

        private void doWork(){
            try {
                Thread. sleep(workTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}