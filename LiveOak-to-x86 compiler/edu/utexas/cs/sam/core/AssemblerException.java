/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

public class AssemblerException
extends Exception {
    private String message = "Assembler Exception";
    private int line = -1;

    public AssemblerException() {
    }

    public AssemblerException(String msg) {
        this.message = msg;
    }

    public AssemblerException(int lc) {
        this.line = lc;
    }

    public AssemblerException(String msg, int lc) {
        this.message = msg;
        this.line = lc;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public int getLine() {
        return this.line;
    }

    @Override
    public String toString() {
        return this.line > -1 ? String.valueOf(this.message) + ": line " + this.line : this.message;
    }
}

