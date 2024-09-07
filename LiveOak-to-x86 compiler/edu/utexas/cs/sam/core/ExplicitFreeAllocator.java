/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import java.util.Iterator;

public class ExplicitFreeAllocator
implements HeapAllocator {
    private static final boolean DEBUG_ALLOCATOR = false;
    private static final int STATUS_MASK = Integer.MIN_VALUE;
    private static final int SIZE_MASK = Integer.MAX_VALUE;
    private static final int HEAP_BASE = 1000;
    private static final int HEAP_TOP = 10000;
    private static final int HEAP_SIZE = 9000;
    private static final int ANCHOR_BASE = 1000;
    private static final int ANCHOR_TOP = 1032;
    private static final int ANCHOR_SIZE = 32;
    private static final int ALLOC_ANCHOR = 0;
    private static final int METADATA_SIZE = 5;
    private static final int SLICE_SIZE_OFFSET = 0;
    private static final int BIN_PREV_OFFSET = 1;
    private static final int BIN_NEXT_OFFSET = 2;
    private static final int REQ_SIZE_OFFSET = 3;
    private static final int DATA_OFFSET = 4;
    private static final int SLICE_SIZE_END_OFFSET = 1;
    private static final int MIN_SLICE_SIZE = 5;
    private Memory mem = null;

    @Override
    public Memory getMemory() {
        return this.mem;
    }

    @Override
    public void setMemory(Memory mem) {
        this.mem = mem;
    }

    private void printBins(String caller) {
    }

    @Override
    public void init() {
        try {
            if (this.mem == null) {
                return;
            }
            int i = 1000;
            while (i < 1032) {
                this.mem.setMem(i, 0, Memory.Type.MA);
                ++i;
            }
            int ptr = 1032;
            int size = 8968;
            this.distribute(ptr, size);
        }
        catch (SystemException systemException) {
            // empty catch block
        }
        this.printBins("init()");
    }

    private static int getBin(int size) {
        int top_bit = Integer.MIN_VALUE;
        if (size < 5) {
            size = 5;
        }
        int bin_idx = 31;
        while (bin_idx > 0 && (size & top_bit) == 0) {
            size <<= 1;
            --bin_idx;
        }
        if ((size & top_bit - 1) == 0) {
            return bin_idx;
        }
        return bin_idx + 1;
    }

    private void distribute(int ptr, int size) throws SystemException {
        int top_bit = Integer.MIN_VALUE;
        int bin_idx = 31;
        while (bin_idx > 0) {
            if ((size & top_bit) != 0) {
                int bin_size = 1 << bin_idx;
                this.attachToAnchor(bin_idx, ptr);
                this.setSizeStatus(ptr, bin_size, false);
                ptr += bin_size;
            }
            size <<= 1;
            --bin_idx;
        }
    }

    private int detachFromAnchor(int bin_idx) throws SystemException {
        int allocated = this.mem.getValue(bin_idx + 1000);
        int successor = this.mem.getValue(allocated + 2);
        if (successor != 0) {
            this.mem.setMem(successor + 1, -bin_idx, Memory.Type.MA);
        }
        this.mem.setMem(bin_idx + 1000, successor, Memory.Type.MA);
        return allocated;
    }

    private void attachToAnchor(int bin_idx, int addr) throws SystemException {
        int successor = this.mem.getValue(bin_idx + 1000);
        this.mem.setMem(addr + 1, -bin_idx, Memory.Type.MA);
        this.mem.setMem(addr + 2, successor, Memory.Type.MA);
        if (successor != 0) {
            this.mem.setMem(successor + 1, addr, Memory.Type.MA);
        }
        this.mem.setMem(bin_idx + 1000, addr, Memory.Type.MA);
    }

    private void unlinkFromBin(int addr) throws SystemException {
        int prev = this.mem.getValue(addr + 1);
        int next = this.mem.getValue(addr + 2);
        if (prev <= 0) {
            this.detachFromAnchor(-prev);
        } else {
            this.mem.setMem(prev + 2, next, Memory.Type.MA);
            if (next != 0) {
                this.mem.setMem(next + 1, prev, Memory.Type.MA);
            }
        }
    }

    private void setSizeStatus(int addr, int size, boolean used) throws SystemException {
        int size_status = used ? Integer.MIN_VALUE | size : size;
        this.mem.setMem(addr + 0, size_status, Memory.Type.INT);
        this.mem.setMem(addr + size - 1, size_status, Memory.Type.INT);
    }

    private int getSize(int addr) throws SystemException {
        return Integer.MAX_VALUE & this.mem.getValue(addr + 0);
    }

    private int getPrevSize(int addr) throws SystemException {
        return Integer.MAX_VALUE & this.mem.getValue(addr - 1);
    }

    private int getNextSize(int addr) throws SystemException {
        return Integer.MAX_VALUE & this.mem.getValue(addr + this.getSize(addr) + 0);
    }

    private boolean isUsed(int addr) throws SystemException {
        return (Integer.MIN_VALUE & this.mem.getValue(addr + 0)) != 0;
    }

    private boolean isPrevUsed(int addr) throws SystemException {
        return (Integer.MIN_VALUE & this.mem.getValue(addr - 1)) != 0;
    }

    private boolean isNextUsed(int addr) throws SystemException {
        return (Integer.MIN_VALUE & this.mem.getValue(addr + this.getSize(addr) + 0)) != 0;
    }

    @Override
    public void malloc(int req_size) throws SystemException {
        int best_fit_idx;
        if (this.mem == null) {
            return;
        }
        if (req_size < 0) {
            return;
        }
        int size = req_size + 5;
        int true_idx = best_fit_idx = ExplicitFreeAllocator.getBin(size);
        while (true_idx < 32 && this.mem.getValue(1000 + true_idx) == 0) {
            ++true_idx;
        }
        if (true_idx == 32) {
            throw new SystemException("malloc(): Insufficient memory");
        }
        int allocated = this.detachFromAnchor(true_idx);
        int used_size = 1 << best_fit_idx;
        int remaining_size = this.getSize(allocated) - used_size;
        this.setSizeStatus(allocated, used_size, true);
        this.mem.setMem(allocated + 3, req_size, Memory.Type.INT);
        this.attachToAnchor(0, allocated);
        int remaining_addr = allocated + used_size;
        if (remaining_size >= 10) {
            this.distribute(remaining_addr, remaining_size);
        } else {
            this.setSizeStatus(allocated, used_size + remaining_size, true);
        }
        this.mem.pushMA(allocated + 4);
        this.printBins("malloc(" + req_size + " -> " + size + ")");
    }

    @Override
    public void free(int req_addr) throws SystemException {
        int free_size;
        int addr = req_addr - 4;
        if (this.mem == null) {
            return;
        }
        if (req_addr < 0 || req_addr > 10000) {
            throw new SystemException("free(): Attempted to free invalid address " + req_addr);
        }
        if (req_addr < 1000) {
            throw new SystemException("free(): Attempted to free stack address " + req_addr);
        }
        if (!this.isUsed(addr)) {
            throw new SystemException("free(): Address " + req_addr + " is already free");
        }
        int free_start = addr;
        int this_size = free_size = this.getSize(addr);
        int ptr = addr;
        while (ptr > 1032 && !this.isPrevUsed(ptr)) {
            int prev_size = this.getPrevSize(ptr);
            free_size += prev_size;
            this.unlinkFromBin(ptr - prev_size);
            ptr = free_start -= prev_size;
        }
        ptr = addr;
        int ptr_size = this_size;
        while (ptr + ptr_size < 10000 && !this.isNextUsed(ptr)) {
            int next_size = this.getNextSize(ptr);
            free_size += next_size;
            this.unlinkFromBin(ptr + ptr_size);
            ptr += ptr_size;
            ptr_size = next_size;
        }
        this.unlinkFromBin(addr);
        this.distribute(free_start, free_size);
        this.printBins("free(" + req_addr + " -> " + addr + ")");
    }

    @Override
    public Iterator<HeapAllocator.Allocation> getAllocations() {
        return new Iterator<HeapAllocator.Allocation>(){
            private int current_addr = 1000;
            private boolean first = true;

            @Override
            public boolean hasNext() {
                if (ExplicitFreeAllocator.this.mem == null) {
                    return false;
                }
                try {
                    if (this.first) {
                        return ExplicitFreeAllocator.this.mem.getValue(this.current_addr) != 0;
                    }
                    return ExplicitFreeAllocator.this.mem.getValue(this.current_addr + 2) != 0;
                }
                catch (SystemException e) {
                    return false;
                }
            }

            @Override
            public HeapAllocator.Allocation next() {
                block5: {
                    try {
                        if (this.hasNext()) break block5;
                        return null;
                    }
                    catch (SystemException e) {
                        return null;
                    }
                }
                if (this.first) {
                    this.first = false;
                    this.current_addr = ExplicitFreeAllocator.this.mem.getValue(this.current_addr);
                } else {
                    this.current_addr = ExplicitFreeAllocator.this.mem.getValue(this.current_addr + 2);
                }
                return new HeapAllocator.Allocation(this.current_addr + 4, ExplicitFreeAllocator.this.mem.getValue(this.current_addr + 3));
            }

            @Override
            public void remove() {
            }
        };
    }
}

