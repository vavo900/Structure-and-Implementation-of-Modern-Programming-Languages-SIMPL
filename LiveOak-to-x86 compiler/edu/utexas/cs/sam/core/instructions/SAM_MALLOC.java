/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_MALLOC
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        HeapAllocator heap = this.mem.getHeapAllocator();
        if (heap != null) {
            heap.malloc(this.mem.popINT());
        } else {
            this.mem.pushINT(0);
        }
        this.cpu.inc(0);
    }
}

