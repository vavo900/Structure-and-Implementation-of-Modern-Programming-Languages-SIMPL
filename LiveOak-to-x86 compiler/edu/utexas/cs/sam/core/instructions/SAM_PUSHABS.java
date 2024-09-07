/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamIntInstruction;

public class SAM_PUSHABS
extends SamIntInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.push(this.mem.getMem(this.op));
        this.cpu.inc(0);
    }
}

