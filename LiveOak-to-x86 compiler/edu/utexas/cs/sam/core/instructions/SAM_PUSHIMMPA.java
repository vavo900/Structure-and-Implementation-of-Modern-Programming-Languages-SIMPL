/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamAddressInstruction;

public class SAM_PUSHIMMPA
extends SamAddressInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushPA(this.op);
        this.cpu.inc(0);
    }
}

