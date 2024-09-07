/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.ReferenceTable;
import edu.utexas.cs.sam.core.SymbolTable;
import edu.utexas.cs.sam.core.instructions.Instruction;
import java.util.List;

public interface Program {
    public void addInst(Instruction var1);

    public void addInst(Instruction[] var1);

    public Instruction getInst(int var1);

    public List<Instruction> getInstList();

    public int getLength();

    public SymbolTable getSymbolTable();

    public ReferenceTable getReferenceTable();

    public void setSymbolTable(SymbolTable var1);

    public void setReferenceTable(ReferenceTable var1);

    public boolean isExecutable();

    public void resolveReferences();

    public void resolveReferencesFrom(Program var1);
}

