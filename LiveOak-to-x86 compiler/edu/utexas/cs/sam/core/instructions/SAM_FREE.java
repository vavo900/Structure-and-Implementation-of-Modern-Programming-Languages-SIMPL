/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_FREE
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        HeapAllocator heap = this.mem.getHeapAllocator();
        int addr = this.mem.popMA();
        if (heap != null) {
            heap.free(addr);
        }
        this.cpu.inc(0);
    }
}

