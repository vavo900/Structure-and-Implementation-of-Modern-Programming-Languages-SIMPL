/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_AND
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushINT(this.mem.popINT() == 0 | this.mem.popINT() == 0 ? 0 : 1);
        this.cpu.inc(0);
    }
}

