/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_DIV
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        int divisor = this.mem.popINT();
        int divided = this.mem.popINT();
        try {
            this.mem.pushINT(divided / divisor);
        }
        catch (ArithmeticException e) {
            throw new SystemException("Attempted division by zero.");
        }
        this.cpu.inc(0);
    }
}

