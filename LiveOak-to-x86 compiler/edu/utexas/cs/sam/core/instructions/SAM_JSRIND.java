/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_JSRIND
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        int top = this.mem.popPA();
        this.mem.pushPA(this.cpu.get(0) + 1);
        this.cpu.set(0, top);
    }
}

