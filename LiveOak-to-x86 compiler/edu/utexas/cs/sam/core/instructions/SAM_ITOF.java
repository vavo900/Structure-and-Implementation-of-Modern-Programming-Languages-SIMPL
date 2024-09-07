/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_ITOF
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        this.mem.pushFLOAT(new Integer(this.mem.popINT()).floatValue());
        this.cpu.inc(0);
    }
}

