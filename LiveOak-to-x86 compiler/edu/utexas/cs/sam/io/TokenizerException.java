/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.io;

public class TokenizerException
extends RuntimeException {
    private String message = "Tokenizer Exception";
    private int line = -1;

    public TokenizerException() {
    }

    public TokenizerException(String msg) {
        this.message = msg;
    }

    public TokenizerException(int lc) {
        this.line = lc;
    }

    public TokenizerException(String msg, int lc) {
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

