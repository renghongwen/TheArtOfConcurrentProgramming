package com.rhw.java._4_Lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class _01_CustomLock implements Lock {

    //AQS
    private static class Sync extends AbstractQueuedSynchronizer{
        /**
         * 互斥锁加锁 使用state来判断线程是否加锁 0:未加锁 1:加锁
         * @param arg
         * @return
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0, arg)){
                //设置当前线程为独有线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 互斥锁释放锁
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            if(getState() == 0){
                throw new IllegalMonitorStateException();
            }
            setState(0);//直接将锁状态写成0,执行该方法说明当前线程持有锁
            return true;
        }

        Condition newCondtion(){
            return new ConditionObject();
        }

        boolean isLock(){
            return getState() == 1;
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    @Override
    public void unlock() {
        sync.release(0);
    }
    @Override
    public Condition newCondition() {
        return sync.newCondtion();
    }

    /**
     * 判断一个线程是否已经加锁
     * @return
     */
    public boolean isLock(){
        return sync.isLock();
    }

    public boolean isHeldExclusively(){
        return false;
    }
}
