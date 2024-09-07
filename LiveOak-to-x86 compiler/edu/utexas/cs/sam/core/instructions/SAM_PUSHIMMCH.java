/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamCharInstruction;

public class SAM_PUSHIMMCH
extends SamCharInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushCH(this.op);
        this.cpu.inc(0);
    }
}

