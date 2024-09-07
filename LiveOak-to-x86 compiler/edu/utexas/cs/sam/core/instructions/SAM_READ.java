/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_READ
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        if (this.video != null) {
            this.mem.pushINT(this.video.readInt());
        } else {
            this.mem.pushINT(0);
        }
        this.cpu.inc(0);
    }
}

