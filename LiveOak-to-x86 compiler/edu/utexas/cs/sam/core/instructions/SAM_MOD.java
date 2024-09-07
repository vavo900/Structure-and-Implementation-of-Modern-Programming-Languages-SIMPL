/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_MOD
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        long v1 = this.mem.popINT();
        long v2 = this.mem.popINT();
        this.mem.pushINT((int)(v2 % v1));
        this.cpu.inc(0);
    }
}

