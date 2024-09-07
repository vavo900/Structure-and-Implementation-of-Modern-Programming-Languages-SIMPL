/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamIntInstruction;

public class SAM_ADDSP
extends SamIntInstruction {
    @Override
    public void exec() throws SystemException {
        this.cpu.set(1, this.cpu.get(1) + this.op);
        this.cpu.inc(0);
    }
}

