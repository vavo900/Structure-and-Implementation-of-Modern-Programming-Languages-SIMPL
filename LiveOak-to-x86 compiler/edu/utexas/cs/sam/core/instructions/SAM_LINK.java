/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_LINK
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushMA(this.cpu.get(2));
        this.cpu.set(2, this.cpu.get(1) - 1);
        this.cpu.inc(0);
    }
}

