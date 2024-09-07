/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_CMP
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        int low;
        int high = this.mem.popINT();
        this.mem.pushINT(high > (low = this.mem.popINT()) ? 1 : (high < low ? -1 : 0));
        this.cpu.inc(0);
    }
}

