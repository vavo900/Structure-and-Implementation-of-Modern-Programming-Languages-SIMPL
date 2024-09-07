/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

public class SystemException
extends Exception {
    private String message = "Stack Machine Exception";

    public SystemException() {
    }

    public SystemException(String msg) {
        this.message = msg;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

