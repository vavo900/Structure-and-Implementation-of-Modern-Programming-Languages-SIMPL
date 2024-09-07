/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.utils;

import edu.utexas.cs.sam.core.Memory;
import java.io.Serializable;
import java.util.List;

public class ProgramState
implements Serializable {
    private List<? extends Memory.Data> stack;
    private int[] registers;
    private int lastpc;

    public ProgramState(int lastpc, List<? extends Memory.Data> stack, int[] regs) {
        this.lastpc = lastpc;
        this.stack = stack;
        this.registers = regs;
    }

    public int getLastPC() {
        return this.lastpc;
    }

    public int[] getRegisters() {
        return this.registers;
    }

    public List<? extends Memory.Data> getStack() {
        return this.stack;
    }
}

