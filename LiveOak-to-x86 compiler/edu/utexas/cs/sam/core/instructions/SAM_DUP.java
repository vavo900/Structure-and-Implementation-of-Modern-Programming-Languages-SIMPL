/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_DUP
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        Memory.Data top = this.mem.pop();
        this.mem.push(top);
        this.mem.push(top);
        this.cpu.inc(0);
    }
}

