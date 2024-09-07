/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.instructions.SamInstruction;

public abstract class SamCharInstruction
extends SamInstruction {
    protected char op;

    @Override
    public String toString() {
        return String.valueOf(this.name) + " " + '\'' + this.op + '\'';
    }

    public char getOperand() {
        return this.op;
    }

    public void setOperand(char operand) {
        this.op = operand;
    }
}

