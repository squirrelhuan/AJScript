package cn.demomaster.ajscript.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockQueue<T> {
    private Object queue[];
    private int front;
    private int rear;
    private int maxSize;

    final private Lock lock = new ReentrantLock();
    Condition full = lock.newCondition();
    Condition empty = lock.newCondition();

    public BlockQueue(int maxSize) {
        this.front = 0;
        this.rear = 0;
        this.maxSize = maxSize;
        this.queue = new Object[maxSize];
    }

    /**
     * 阻塞入队
     * @param element
     */
    public void put(T element) throws InterruptedException {
        lock.lock();
        try{
            while ( (rear + 1) % maxSize == front ) {
                System.out.println("Queue is full");
                full.await();
            }
            queue[rear] = element;
            rear = (rear + 1) % maxSize;
            empty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞出队
     */
    public T take() throws InterruptedException{
        lock.lock();
        try{
            while( rear == front ){
                System.out.println("Queue is empty");
                empty.await();
            }
            Object element = queue[front];
            queue[front] = null;
            front = (front+1)%maxSize;
            full.signal();
            return (T) element;
        }finally {
            lock.unlock();
        }
    }
}
