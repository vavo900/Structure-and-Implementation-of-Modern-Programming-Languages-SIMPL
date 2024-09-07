/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamIntInstruction;

public class SAM_STOREOFF
extends SamIntInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.setMem(this.cpu.get(2) + this.op, this.mem.pop());
        this.cpu.inc(0);
    }
}

