/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import java.util.Collection;

public interface SymbolTable {
    public void add(String var1, int var2);

    public String resolveSymbol(int var1);

    public Collection<String> resolveSymbols(int var1);

    public int resolveAddress(String var1);

    public Collection<String> getSymbols();

    public String toString();
}

