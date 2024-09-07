/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_CMPF
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        float low;
        float high = this.mem.popFLOAT();
        this.mem.pushINT(high > (low = this.mem.popFLOAT()) ? 1 : (high < low ? -1 : 0));
        this.cpu.inc(0);
    }
}

