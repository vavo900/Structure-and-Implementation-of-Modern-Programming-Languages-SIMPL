/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.instructions.SamInstruction;

public abstract class SamIntInstruction
extends SamInstruction {
    protected int op;

    @Override
    public String toString() {
        return String.valueOf(this.name) + " " + this.op;
    }

    public int getOperand() {
        return this.op;
    }

    public void setOperand(int operand) {
        this.op = operand;
    }
}

