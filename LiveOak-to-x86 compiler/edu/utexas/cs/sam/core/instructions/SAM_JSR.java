/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamAddressInstruction;

public class SAM_JSR
extends SamAddressInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushPA(this.cpu.get(0) + 1);
        this.cpu.set(0, this.op);
    }
}

