/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;

public interface Processor {
    public static final int PC = 0;
    public static final int SP = 1;
    public static final int FBR = 2;
    public static final int HALT = 3;

    public void load(Program var1) throws SystemException;

    public Program getProgram();

    public Sys getSystem();

    public void step() throws SystemException;

    public void run() throws SystemException;

    public void init();

    public int get(int var1);

    public int[] getRegisters();

    public void set(int var1, int var2) throws SystemException;

    public int inc(int var1) throws SystemException;

    public int dec(int var1) throws SystemException;

    public void verify(int var1, int var2) throws SystemException;
}

