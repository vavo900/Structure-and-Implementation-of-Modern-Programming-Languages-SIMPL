/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_PUSHSP
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushMA(this.cpu.get(1));
        this.cpu.inc(0);
    }
}

