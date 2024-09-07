/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_POPSP
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.cpu.set(1, this.mem.popMA());
        this.cpu.inc(0);
    }
}

