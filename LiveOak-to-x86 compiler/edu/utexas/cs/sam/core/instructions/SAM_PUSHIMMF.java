/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamFloatInstruction;

public class SAM_PUSHIMMF
extends SamFloatInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushFLOAT(this.op);
        this.cpu.inc(0);
    }
}

