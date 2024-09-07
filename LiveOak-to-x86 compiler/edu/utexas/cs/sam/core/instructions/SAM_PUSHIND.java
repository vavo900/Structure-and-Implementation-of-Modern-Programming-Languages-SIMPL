/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_PUSHIND
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.push(this.mem.getMem(this.mem.popMA()));
        this.cpu.inc(0);
    }
}

