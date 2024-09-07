/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.io;

import edu.utexas.cs.sam.io.TokenizerException;

public interface Tokenizer {
    public TokenType peekAtKind();

    public int getInt() throws TokenizerException;

    public float getFloat() throws TokenizerException;

    public String getWord() throws TokenizerException;

    public String getString() throws TokenizerException;

    public char getCharacter() throws TokenizerException;

    public char getOp() throws TokenizerException;

    public String getComment() throws TokenizerException;

    public void match(char var1) throws TokenizerException;

    public void match(String var1) throws TokenizerException;

    public boolean check(char var1);

    public boolean check(String var1);

    public boolean test(char var1);

    public boolean test(String var1);

    public void pushBack();

    public boolean canPushBack();

    public void skipToken();

    public String getWhitespaceBeforeToken();

    public int lineNo();

    public int nextLineNo();

    public void close();

    public static enum TokenType {
        INTEGER,
        FLOAT,
        WORD,
        STRING,
        CHARACTER,
        OPERATOR,
        COMMENT,
        EOF;

    }
}

