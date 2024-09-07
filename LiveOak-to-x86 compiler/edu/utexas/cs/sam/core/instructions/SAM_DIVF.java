/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_DIVF
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        float divisor = this.mem.popFLOAT();
        float divided = this.mem.popFLOAT();
        this.mem.pushFLOAT(divided / divisor);
        this.cpu.inc(0);
    }
}

