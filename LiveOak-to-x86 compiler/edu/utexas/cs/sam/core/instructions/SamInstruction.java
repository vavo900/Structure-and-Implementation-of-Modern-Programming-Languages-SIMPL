/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core.instructions;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.Video;
import edu.utexas.cs.sam.core.instructions.Instruction;
import java.io.Serializable;

public abstract class SamInstruction
implements Instruction,
Serializable {
    private final Package pkg = this.getClass().getPackage();
    private final String prefix = "SAM_";
    protected final String name = this.getClass().getName().substring(this.pkg == null ? "SAM_".length() : this.pkg.getName().length() + 1 + "SAM_".length());
    protected transient Program prog;
    protected transient Processor cpu = null;
    protected transient Memory mem = null;
    protected transient Video video = null;
    protected transient Sys sys;
    protected static final int PC = 0;
    protected static final int SP = 1;
    protected static final int HALT = 3;
    protected static final int FBR = 2;

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setSystem(Sys sys) {
        this.sys = sys;
        this.cpu = sys.cpu();
        this.mem = sys.mem();
        this.video = sys.video();
    }

    @Override
    public Sys getSystem() {
        return this.sys;
    }

    @Override
    public void setProgram(Program p) {
        this.prog = p;
    }

    @Override
    public Program getProgram() {
        return this.prog;
    }

    @Override
    public abstract void exec() throws SystemException;
}

