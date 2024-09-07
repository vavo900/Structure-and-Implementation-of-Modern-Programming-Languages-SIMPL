/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.io;

import edu.utexas.cs.sam.io.TokenizerException;

public class TokenParseException
extends TokenizerException {
    public TokenParseException(String msg, int lc) {
        super(msg, lc);
    }
}

