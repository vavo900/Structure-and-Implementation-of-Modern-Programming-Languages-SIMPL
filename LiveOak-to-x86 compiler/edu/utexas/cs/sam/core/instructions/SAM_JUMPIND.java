/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_JUMPIND
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.cpu.set(0, this.mem.popPA());
    }
}

