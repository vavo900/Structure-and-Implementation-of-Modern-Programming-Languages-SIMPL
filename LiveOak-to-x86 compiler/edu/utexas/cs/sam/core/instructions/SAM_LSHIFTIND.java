/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_LSHIFTIND
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        int bits = this.mem.popINT();
        this.mem.pushINT(this.mem.popINT() << bits);
        this.cpu.inc(0);
    }
}

