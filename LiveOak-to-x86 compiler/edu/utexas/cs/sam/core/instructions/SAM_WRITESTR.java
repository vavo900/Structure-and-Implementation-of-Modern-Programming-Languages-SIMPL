/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_WRITESTR
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        char ch;
        String str = "";
        int addr = this.mem.popMA();
        while ((ch = (char)this.mem.getValue(addr++)) != '\u0000') {
            str = String.valueOf(str) + ch;
        }
        if (this.video != null) {
            this.video.writeString(str);
        }
        this.cpu.inc(0);
    }
}

