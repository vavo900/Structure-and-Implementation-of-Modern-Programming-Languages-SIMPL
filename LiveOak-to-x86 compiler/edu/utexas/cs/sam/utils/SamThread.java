/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.utils;

public abstract class SamThread
extends Thread {
    private volatile boolean stopRequested = false;
    private ThreadParent parent = null;
    public static final int THREAD_INTERRUPTED = 0;
    public static final int THREAD_EXCEPTION = 1;
    public static final int THREAD_EXIT_OK = 2;

    public void setParent(ThreadParent parent) {
        this.parent = parent;
    }

    public ThreadParent getParent() {
        return this.parent;
    }

    @Override
    public void interrupt() {
        this.stopRequested = true;
        super.interrupt();
    }

    public boolean interruptRequested() {
        return this.stopRequested;
    }

    @Override
    public void run() {
        try {
            this.execute();
        }
        catch (Exception e) {
            this.parent.threadEvent(1, e);
            return;
        }
    }

    public abstract void execute() throws Exception;

    public static interface ThreadParent {
        public void threadEvent(int var1, Object var2);
    }
}

