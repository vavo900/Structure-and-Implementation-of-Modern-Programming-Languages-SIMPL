/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamAddressInstruction;

public class SAM_JUMPC
extends SamAddressInstruction {
    @Override
    public void exec() throws SystemException {
        if (this.mem.popINT() != 0) {
            this.cpu.set(0, this.op);
        } else {
            this.cpu.inc(0);
        }
    }
}

