/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.ReferenceTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class SamReferenceTable
implements ReferenceTable,
Serializable {
    private static final String BR = System.getProperty("line.separator");
    private HashMap<String, ArrayList<Integer>> references = new HashMap();

    @Override
    public void add(String symbol, int ref_address) {
        ArrayList<Integer> srefs = this.references.get(symbol);
        if (srefs == null) {
            srefs = new ArrayList();
            this.references.put(symbol, srefs);
        }
        srefs.add(ref_address);
    }

    @Override
    public void deleteSymbol(String symbol) {
        this.references.remove(symbol);
    }

    @Override
    public Collection<Integer> getReferences(String symbol) {
        return this.references.get(symbol);
    }

    @Override
    public int size() {
        return this.references.size();
    }

    @Override
    public String toString() {
        String ret = new String();
        Set<String> symbols = this.references.keySet();
        for (String symbol : symbols) {
            ret = String.valueOf(ret) + "Symbol \"" + symbol + "\" at addresses: ";
            ArrayList<Integer> srefs = this.references.get(symbol);
            for (Integer i : this.references.get(symbol)) {
                ret = String.valueOf(ret) + i + " ";
            }
            ret = String.valueOf(ret) + BR;
        }
        return ret;
    }
}

