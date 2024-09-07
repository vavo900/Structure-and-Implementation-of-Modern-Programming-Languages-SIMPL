/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_READSTR
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        String str = this.video != null ? this.video.readString() : "";
        int size = str.length();
        HeapAllocator heap = this.mem.getHeapAllocator();
        if (heap == null) {
            this.mem.pushMA(0);
        } else {
            heap.malloc(size + 1);
            int addr = this.mem.getValue(this.cpu.get(1) - 1);
            int a = 0;
            while (a < size) {
                this.mem.setMem(addr + a, str.charAt(a), Memory.Type.CH);
                ++a;
            }
            this.mem.setMem(addr + a, 0, Memory.Type.CH);
        }
        this.cpu.inc(0);
    }
}

