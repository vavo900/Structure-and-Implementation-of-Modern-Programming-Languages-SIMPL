/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import java.util.Iterator;

public interface HeapAllocator {
    public void init();

    public void malloc(int var1) throws SystemException;

    public void free(int var1) throws SystemException;

    public void setMemory(Memory var1);

    public Memory getMemory();

    public Iterator<Allocation> getAllocations();

    public static class Allocation {
        private int addr;
        private int size;

        public Allocation(int addr, int size) {
            this.addr = addr;
            this.size = size;
        }

        public int getAddr() {
            return this.addr;
        }

        public int getSize() {
            return this.size;
        }
    }
}

