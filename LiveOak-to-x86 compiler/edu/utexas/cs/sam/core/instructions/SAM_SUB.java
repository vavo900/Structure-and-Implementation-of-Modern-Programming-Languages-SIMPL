/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.SamInstruction;

public class SAM_SUB
extends SamInstruction {
    @Override
    public void exec() throws SystemException {
        Memory.Data two = this.mem.pop();
        Memory.Data one = this.mem.pop();
        Memory.Type t1 = one.getType();
        Memory.Type t2 = two.getType();
        Memory.Type t_new = t1 == Memory.Type.MA && t2 == Memory.Type.INT || t1 == Memory.Type.INT && t2 == Memory.Type.MA ? Memory.Type.MA : (t1 == Memory.Type.PA && t2 == Memory.Type.INT || t1 == Memory.Type.INT && t2 == Memory.Type.PA ? Memory.Type.PA : Memory.Type.INT);
        Memory.Data result = new Memory.Data(one.getValue() - two.getValue(), t_new);
        this.mem.push(result);
        this.cpu.inc(0);
    }
}

