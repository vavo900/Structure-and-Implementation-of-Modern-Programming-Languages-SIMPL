/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.SymbolTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SamSymbolTable
implements SymbolTable,
Serializable {
    private HashMap<String, Integer> sym2adr = new HashMap();
    private HashMap<Integer, ArrayList<String>> adr2sym = new HashMap();

    @Override
    public void add(String symbol, int address) {
        Integer adr = new Integer(address);
        this.sym2adr.put(symbol, adr);
        if (this.adr2sym.containsKey(adr)) {
            ArrayList<String> v = this.adr2sym.get(adr);
            if (!v.contains(symbol)) {
                v.add(symbol);
            }
        } else {
            ArrayList<String> v = new ArrayList<String>();
            v.add(symbol);
            this.adr2sym.put(adr, v);
        }
    }

    @Override
    public Collection<String> resolveSymbols(int address) {
        return this.adr2sym.get(address);
    }

    @Override
    public String resolveSymbol(int address) {
        ArrayList<String> labels = this.adr2sym.get(address);
        return labels != null ? labels.get(0) : null;
    }

    @Override
    public int resolveAddress(String label) {
        Integer addr = this.sym2adr.get(label);
        return addr != null ? addr : -1;
    }

    @Override
    public Collection<String> getSymbols() {
        return this.sym2adr.keySet();
    }

    @Override
    public String toString() {
        return this.sym2adr.toString();
    }
}

