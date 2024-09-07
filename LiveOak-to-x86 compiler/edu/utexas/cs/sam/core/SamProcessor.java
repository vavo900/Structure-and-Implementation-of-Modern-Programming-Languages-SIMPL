/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.instructions.Instruction;

public class SamProcessor
implements Processor {
    public static final int REGISTERS = 4;
    private int[] registers = new int[4];
    private static final String BR = System.getProperty("line.separator");
    private Program prg;
    private Sys sys;

    public SamProcessor(Sys sys) {
        this.sys = sys;
    }

    @Override
    public Sys getSystem() {
        return this.sys;
    }

    @Override
    public void load(Program prog) throws SystemException {
        if (!prog.isExecutable()) {
            throw new SystemException("Program contains unresolved references: " + BR + prog.getReferenceTable().toString());
        }
        this.prg = prog;
    }

    @Override
    public Program getProgram() {
        return this.prg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void step() throws SystemException {
        Program program = this.prg;
        synchronized (program) {
            Instruction i = this.prg.getInst(this.registers[0]);
            i.setSystem(this.sys);
            i.exec();
        }
    }

    @Override
    public void run() throws SystemException {
        while (this.registers[3] == 0) {
            this.step();
        }
    }

    @Override
    public void init() {
        int i = 0;
        while (i < 4) {
            this.registers[i] = 0;
            ++i;
        }
    }

    @Override
    public int get(int reg) {
        return this.registers[reg];
    }

    @Override
    public int[] getRegisters() {
        int[] regs = new int[4];
        System.arraycopy(this.registers, 0, regs, 0, 4);
        return regs;
    }

    @Override
    public void set(int reg, int value) throws SystemException {
        this.verify(reg, value);
        this.registers[reg] = value;
    }

    @Override
    public int inc(int reg) throws SystemException {
        this.verify(reg, this.registers[reg] + 1);
        int n = reg;
        int n2 = this.registers[n] + 1;
        this.registers[n] = n2;
        return n2;
    }

    @Override
    public int dec(int reg) throws SystemException {
        this.verify(reg, this.registers[reg] - 1);
        int n = reg;
        int n2 = this.registers[n] - 1;
        this.registers[n] = n2;
        return n2;
    }

    @Override
    public void verify(int reg, int value) throws SystemException {
        switch (reg) {
            case 0: {
                if (value >= 0 && value <= this.prg.getLength() - 1) break;
                throw new SystemException("Invalid instruction index, PC = " + value);
            }
            case 1: {
                if (value < 0) {
                    throw new SystemException("Stack Underflow, SP = " + value);
                }
                if (value <= 999) break;
                throw new SystemException("Stack Overflow, SP = " + value);
            }
            case 2: {
                if (value >= 0 && value <= 999) break;
                throw new SystemException("Invalid frame address, FBR = " + value);
            }
        }
    }
}

