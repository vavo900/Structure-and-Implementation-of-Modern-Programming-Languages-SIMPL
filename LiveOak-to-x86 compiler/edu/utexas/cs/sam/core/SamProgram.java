/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.ReferenceTable;
import edu.utexas.cs.sam.core.SamReferenceTable;
import edu.utexas.cs.sam.core.SamSymbolTable;
import edu.utexas.cs.sam.core.SymbolTable;
import edu.utexas.cs.sam.core.instructions.Instruction;
import edu.utexas.cs.sam.core.instructions.SamIntInstruction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SamProgram
implements Program,
Serializable {
    private List<Instruction> instructions = new ArrayList<Instruction>();
    private SymbolTable syms = new SamSymbolTable();
    private ReferenceTable refs = new SamReferenceTable();
    private static final String BR = System.getProperty("line.separator");

    @Override
    public Instruction getInst(int pos) {
        return this.instructions.get(pos);
    }

    @Override
    public List<Instruction> getInstList() {
        return this.instructions;
    }

    @Override
    public void addInst(Instruction i) {
        this.instructions.add(i);
    }

    @Override
    public void addInst(Instruction[] arr) {
        int a = 0;
        while (a < arr.length) {
            this.instructions.add(arr[a]);
            ++a;
        }
    }

    @Override
    public int getLength() {
        return this.instructions.size();
    }

    @Override
    public SymbolTable getSymbolTable() {
        return this.syms;
    }

    @Override
    public ReferenceTable getReferenceTable() {
        return this.refs;
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        this.syms = table;
    }

    @Override
    public void setReferenceTable(ReferenceTable table) {
        this.refs = table;
    }

    private static void resolve(SymbolTable syms, ReferenceTable refs, List<Instruction> instructions) {
        Collection<String> symbols = syms.getSymbols();
        for (String sym : symbols) {
            int address = syms.resolveAddress(sym);
            Collection<Integer> references = refs.getReferences(sym);
            if (references != null) {
                for (Integer ref : references) {
                    ((SamIntInstruction)instructions.get(ref)).setOperand(address);
                }
            }
            refs.deleteSymbol(sym);
        }
    }

    @Override
    public void resolveReferences() {
        SamProgram.resolve(this.syms, this.refs, this.instructions);
    }

    @Override
    public void resolveReferencesFrom(Program prog) {
        SamProgram.resolve(this.syms, prog.getReferenceTable(), prog.getInstList());
    }

    @Override
    public boolean isExecutable() {
        return this.refs.size() == 0;
    }

    public String toString() {
        String toReturn = "Instructions: " + BR;
        int i = 0;
        while (i < this.instructions.size()) {
            toReturn = String.valueOf(toReturn) + i + ": " + this.instructions.get(i) + BR;
            ++i;
        }
        toReturn = String.valueOf(toReturn) + BR + "Symbol Table:" + BR + this.syms.toString() + BR + "Reference Table:" + BR + this.refs.toString();
        return toReturn;
    }
}

