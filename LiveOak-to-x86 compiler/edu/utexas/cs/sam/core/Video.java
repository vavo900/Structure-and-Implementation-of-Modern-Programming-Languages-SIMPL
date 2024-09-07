/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

public interface Video {
    public void writeInt(int var1);

    public void writeString(String var1);

    public void writeFloat(float var1);

    public void writeChar(char var1);

    public int readInt();

    public float readFloat();

    public char readChar();

    public String readString();
}

