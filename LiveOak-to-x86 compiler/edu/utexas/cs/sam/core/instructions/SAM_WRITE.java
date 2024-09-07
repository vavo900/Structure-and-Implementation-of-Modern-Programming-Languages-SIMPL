/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_WRITE
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        if (this.video != null) {
            this.video.writeInt(this.mem.popINT());
        } else {
            this.mem.popINT();
        }
        this.cpu.inc(0);
    }
}

