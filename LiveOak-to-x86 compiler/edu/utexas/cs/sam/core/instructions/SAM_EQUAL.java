/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_EQUAL
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushINT(this.mem.popValue() == this.mem.popValue() ? 1 : 0);
        this.cpu.inc(0);
    }
}

