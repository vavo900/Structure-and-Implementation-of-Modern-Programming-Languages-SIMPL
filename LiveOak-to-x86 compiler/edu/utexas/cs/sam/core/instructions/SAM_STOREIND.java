/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_STOREIND
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        Memory.Data data = this.mem.pop();
        this.mem.setMem(this.mem.popMA(), data);
        this.cpu.inc(0);
    }
}

