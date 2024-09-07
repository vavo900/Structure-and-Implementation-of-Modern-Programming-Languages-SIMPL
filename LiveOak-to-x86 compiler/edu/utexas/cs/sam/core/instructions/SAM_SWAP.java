/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_SWAP
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        Memory.Data two = this.mem.pop();
        Memory.Data one = this.mem.pop();
        this.mem.push(two);
        this.mem.push(one);
        this.cpu.inc(0);
    }
}

