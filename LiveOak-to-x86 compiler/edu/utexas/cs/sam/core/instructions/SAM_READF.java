/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_READF
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        if (this.video != null) {
            this.mem.pushFLOAT(this.video.readFloat());
        } else {
            this.mem.pushFLOAT(0.0f);
        }
        this.cpu.inc(0);
    }
}

