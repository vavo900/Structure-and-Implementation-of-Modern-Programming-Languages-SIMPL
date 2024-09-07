/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_WRITEF
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        if (this.video != null) {
            this.video.writeFloat(this.mem.popFLOAT());
        } else {
            this.mem.popFLOAT();
        }
        this.cpu.inc(0);
    }
}

