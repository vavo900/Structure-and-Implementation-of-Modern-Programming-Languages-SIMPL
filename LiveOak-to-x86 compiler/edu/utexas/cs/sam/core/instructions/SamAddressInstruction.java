/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.instructions.SamIntInstruction;

public abstract class SamAddressInstruction
extends SamIntInstruction {
    @Override
    public String toString() {
        String label = null;
        if (this.prog != null) {
            label = this.prog.getSymbolTable().resolveSymbol(this.op);
        }
        return String.valueOf(this.name) + " " + (label == null ? Integer.toString(this.op) : label);
    }
}

