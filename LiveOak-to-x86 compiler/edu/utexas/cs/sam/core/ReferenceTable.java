/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import java.util.Collection;

public interface ReferenceTable {
    public void add(String var1, int var2);

    public void deleteSymbol(String var1);

    public Collection<Integer> getReferences(String var1);

    public String toString();

    public int size();
}

