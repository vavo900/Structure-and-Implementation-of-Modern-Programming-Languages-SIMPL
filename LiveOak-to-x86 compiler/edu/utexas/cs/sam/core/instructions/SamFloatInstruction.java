/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.instructions.SamInstruction;

public abstract class SamFloatInstruction
extends SamInstruction {
    protected float op;

    @Override
    public String toString() {
        return String.valueOf(this.name) + " " + this.op;
    }

    public float getOperand() {
        return this.op;
    }

    public void setOperand(float operand) {
        this.op = operand;
    }
}

