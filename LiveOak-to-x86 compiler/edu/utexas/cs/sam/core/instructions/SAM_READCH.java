/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_READCH
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        if (this.video != null) {
            this.mem.pushCH(this.video.readChar());
        } else {
            this.mem.pushCH('\u0000');
        }
        this.cpu.inc(0);
    }
}

