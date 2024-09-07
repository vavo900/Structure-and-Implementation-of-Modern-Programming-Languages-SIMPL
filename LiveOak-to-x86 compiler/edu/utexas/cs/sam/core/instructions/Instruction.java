/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;

public interface Instruction {
    public String toString();

    public String getName();

    public void setSystem(Sys var1);

    public Sys getSystem();

    public void setProgram(Program var1);

    public Program getProgram();

    public void exec() throws SystemException;
}

