/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_XOR
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        boolean one = this.mem.popINT() != 0;
        boolean two = this.mem.popINT() != 0;
        this.mem.pushINT(one && !two || two && !one ? 1 : 0);
        this.cpu.inc(0);
    }
}

