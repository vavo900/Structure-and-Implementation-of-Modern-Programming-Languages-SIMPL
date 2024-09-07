/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.instructions.SamInstruction;

public abstract class SamStringInstruction
extends SamInstruction {
    protected String op;

    @Override
    public String toString() {
        return String.valueOf(this.name) + " " + '\"' + this.op + '\"';
    }

    public String getOperand() {
        return this.op;
    }

    public void setOperand(String operand) {
        this.op = operand;
    }
}

