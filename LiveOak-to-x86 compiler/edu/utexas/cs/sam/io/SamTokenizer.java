/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.io;

import edu.utexas.cs.sam.io.TokenParseException;
import edu.utexas.cs.sam.io.Tokenizer;
import edu.utexas.cs.sam.io.TokenizerException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Stack;

public class SamTokenizer
implements Tokenizer {
    private PushbackReader in;
    private Stack<Token> tokens = new Stack();
    private Stack<Token> readTokens = new Stack();
    private boolean processStrings = false;
    private boolean processCharacters = false;
    private boolean processComments = false;

    public SamTokenizer(String FileName, TokenizerOptions ... opt) throws IOException, FileNotFoundException, TokenParseException {
        FileReader r = new FileReader(FileName);
        this.parseOptions(opt);
        this.in = new PushbackReader(r);
        this.read();
    }

    public SamTokenizer(Reader r, TokenizerOptions ... opt) throws IOException, TokenParseException {
        this.in = new PushbackReader(r);
        this.parseOptions(opt);
        this.read();
    }

    public SamTokenizer(TokenizerOptions ... opt) throws IOException, TokenParseException {
        this.in = new PushbackReader(new InputStreamReader(System.in));
        this.parseOptions(opt);
        this.read();
    }

    private void parseOptions(TokenizerOptions ... opt) {
        int i = 0;
        while (i < opt.length) {
            switch (opt[i]) {
                case PROCESS_COMMENTS: {
                    this.processComments = true;
                    break;
                }
                case PROCESS_STRINGS: {
                    this.processStrings = true;
                    break;
                }
                case PROCESS_CHARACTERS: {
                    this.processCharacters = true;
                }
            }
            ++i;
        }
    }

    private void read() throws IOException, TokenParseException {
        int cin;
        int lineNo = 1;
        String whitespace = "";
        while ((cin = this.in.read()) != -1) {
            char c = (char)cin;
            if (c == '\n') {
                ++lineNo;
            }
            if (Character.isWhitespace(c)) {
                whitespace = String.valueOf(whitespace) + c;
                continue;
            }
            if (c == '/') {
                int din = this.in.read();
                if (din == -1) {
                    this.tokens.push(new OperatorToken('/', whitespace, lineNo));
                    whitespace = "";
                    continue;
                }
                if ((char)din != '/') {
                    this.in.unread(din);
                    this.tokens.push(new OperatorToken('/', whitespace, lineNo));
                    whitespace = "";
                    continue;
                }
                String comment = "";
                while ((din = this.in.read()) != -1) {
                    comment = String.valueOf(comment) + din;
                    if (din != 10) continue;
                    this.in.unread(din);
                    break;
                }
                if (!this.processComments) continue;
                this.tokens.push(new CommentToken(comment, whitespace, lineNo));
                whitespace = "";
                continue;
            }
            if (this.processStrings && c == '\"') {
                String str = "";
                while ((cin = this.in.read()) != -1 && cin != 34) {
                    str = String.valueOf(str) + this.readChar(cin, lineNo);
                }
                if (cin == -1) {
                    throw new TokenParseException("Incomplete string token - missing \"", lineNo);
                }
                this.tokens.push(new StringToken(str, whitespace, lineNo));
                whitespace = "";
                continue;
            }
            if (this.processCharacters && c == '\'') {
                cin = this.in.read();
                if (cin == -1) {
                    throw new TokenParseException("Incomplete character token - missing '", lineNo);
                }
                char a = this.readChar(cin, lineNo);
                if (this.in.read() != 39) {
                    throw new TokenParseException("Character token is too long", lineNo);
                }
                this.tokens.push(new CharToken(a, whitespace, lineNo));
                whitespace = "";
                continue;
            }
            if (Character.isLetter(c)) {
                int din;
                String word = "";
                word = String.valueOf(word) + c;
                while ((din = this.in.read()) != -1) {
                    if (Character.isLetter((char)din) || Character.isDigit((char)din) || din == 95) {
                        word = String.valueOf(word) + (char)din;
                        continue;
                    }
                    this.in.unread(din);
                    break;
                }
                this.tokens.push(new WordToken(word, whitespace, lineNo));
                whitespace = "";
                continue;
            }
            if (Character.isDigit(c)) {
                this.in.unread(c);
                this.readNumber(lineNo, whitespace, null);
                whitespace = "";
                continue;
            }
            if (c == '.') {
                int din = this.in.read();
                if (din == -1) {
                    this.tokens.push(new OperatorToken(c, whitespace, lineNo));
                    continue;
                }
                if (Character.isDigit((char)din)) {
                    this.in.unread(din);
                    this.readNumber(lineNo, whitespace, ".");
                    whitespace = "";
                    continue;
                }
                this.in.unread(din);
                this.tokens.push(new OperatorToken(c, whitespace, lineNo));
                whitespace = "";
                continue;
            }
            this.tokens.push(new OperatorToken(c, whitespace, lineNo));
            whitespace = "";
        }
        this.tokens.push(new EOFToken(whitespace, lineNo));
        Stack<Token> a = new Stack<Token>();
        while (!this.tokens.empty()) {
            a.push(this.tokens.pop());
        }
        this.tokens = a;
    }

    private void readNumber(int lineNo, String whitespace, String tok) throws IOException, TokenParseException {
        int din;
        Tokenizer.TokenType type = Tokenizer.TokenType.INTEGER;
        if (tok == null) {
            tok = "";
        } else if (tok.equals(".")) {
            type = Tokenizer.TokenType.FLOAT;
        }
        while ((din = this.in.read()) != -1) {
            if (din == 46) {
                switch (type) {
                    case INTEGER: {
                        type = Tokenizer.TokenType.FLOAT;
                        break;
                    }
                    case FLOAT: {
                        type = Tokenizer.TokenType.WORD;
                    }
                }
                tok = String.valueOf(tok) + (char)din;
                continue;
            }
            if (Character.isDigit((char)din) || type == Tokenizer.TokenType.WORD && (Character.isLetter((char)din) || din == 95)) {
                tok = String.valueOf(tok) + (char)din;
                continue;
            }
            this.in.unread(din);
            break;
        }
        try {
            switch (type) {
                case FLOAT: {
                    this.tokens.push(new FloatToken(Float.parseFloat(tok), whitespace, lineNo));
                    break;
                }
                case INTEGER: {
                    this.tokens.push(new IntToken(Integer.parseInt(tok), whitespace, lineNo));
                    break;
                }
                default: {
                    this.tokens.push(new WordToken(tok, whitespace, lineNo));
                    break;
                }
            }
        }
        catch (NumberFormatException e) {
            throw new TokenParseException("Unparsable number " + tok, lineNo);
        }
    }

    private char readChar(int cin, int lineNo) throws IOException, TokenParseException {
        if (cin == 92) {
            cin = this.in.read();
            if (Character.isDigit(cin)) {
                int code;
                String codeS = "" + (char)cin;
                while (Character.isDigit(cin = this.in.read())) {
                    if ((codeS = String.valueOf(codeS) + (char)cin).length() <= 3) continue;
                    throw new TokenParseException("Invalid escape code " + codeS, lineNo);
                }
                this.in.unread(cin);
                try {
                    code = Integer.parseInt(codeS);
                }
                catch (NumberFormatException e) {
                    throw new TokenParseException("Unparsable number " + codeS, lineNo);
                }
                if (code < 0 || code > 255) {
                    throw new TokenParseException("Invalid escape code " + code, lineNo);
                }
                return (char)code;
            }
            switch (cin) {
                case 116: {
                    return '\t';
                }
                case 92: {
                    return '\\';
                }
                case 110: {
                    return '\n';
                }
                case 114: {
                    return '\r';
                }
                case 34: {
                    return '\"';
                }
                case 39: {
                    return '\'';
                }
            }
            throw new TokenParseException("Invalid escape character '" + cin + "'", lineNo);
        }
        return (char)cin;
    }

    @Override
    public Tokenizer.TokenType peekAtKind() {
        if (this.tokens.empty()) {
            return Tokenizer.TokenType.EOF;
        }
        return this.tokens.peek().getType();
    }

    @Override
    public int getInt() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.INTEGER) {
            IntToken i = (IntToken)this.tokens.pop();
            this.readTokens.push(i);
            return i.getInt();
        }
        throw new TokenizerException("Attempt to read non-integer value as an integer", this.lineNo());
    }

    @Override
    public float getFloat() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.FLOAT) {
            FloatToken f = (FloatToken)this.tokens.pop();
            this.readTokens.push(f);
            return f.getFloat();
        }
        throw new TokenizerException("Attempt to read non-float value as a float", this.lineNo());
    }

    @Override
    public String getWord() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.WORD) {
            WordToken word = (WordToken)this.tokens.pop();
            this.readTokens.push(word);
            return word.getWord();
        }
        throw new TokenizerException("Attempt to read non-word value as a word.", this.lineNo());
    }

    @Override
    public String getString() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.STRING) {
            StringToken str = (StringToken)this.tokens.pop();
            this.readTokens.push(str);
            return str.getString();
        }
        throw new TokenizerException("Attempt to read non-string value as a string.", this.lineNo());
    }

    @Override
    public char getCharacter() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.CHARACTER) {
            CharToken c = (CharToken)this.tokens.pop();
            this.readTokens.push(c);
            return c.getChar();
        }
        throw new TokenizerException("Attempt to read non-char value as a char.", this.lineNo());
    }

    @Override
    public char getOp() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.OPERATOR) {
            OperatorToken op = (OperatorToken)this.tokens.pop();
            this.readTokens.push(op);
            return op.getOp();
        }
        throw new TokenizerException("Attempt to read non-operator value as an op", this.lineNo());
    }

    @Override
    public String getComment() throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.COMMENT) {
            CommentToken c = (CommentToken)this.tokens.pop();
            this.readTokens.push(c);
            return c.getComment();
        }
        throw new TokenizerException("Attempt to read non-comment value as a comment", this.lineNo());
    }

    @Override
    public void match(char c) throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.OPERATOR) {
            char n = this.getOp();
            if (n != c) {
                throw new TokenizerException("Expecting " + c + " but found " + n, this.lineNo());
            }
        } else {
            throw new TokenizerException("Did not find " + c, this.lineNo());
        }
    }

    @Override
    public void match(String s) throws TokenizerException {
        if (this.peekAtKind() == Tokenizer.TokenType.WORD) {
            String n = this.getWord();
            if (!n.equals(s)) {
                throw new TokenizerException("Expecting " + s + " but found " + n, this.lineNo());
            }
        } else {
            throw new TokenizerException("Did not find " + s, this.lineNo());
        }
    }

    @Override
    public boolean check(char c) {
        if (this.peekAtKind() == Tokenizer.TokenType.OPERATOR) {
            if (c == this.getOp()) {
                return true;
            }
            this.pushBack();
            return false;
        }
        return false;
    }

    @Override
    public boolean check(String s) {
        if (this.peekAtKind() == Tokenizer.TokenType.WORD) {
            if (s.equals(this.getWord())) {
                return true;
            }
            this.pushBack();
            return false;
        }
        return false;
    }

    @Override
    public boolean test(char c) {
        boolean check = this.check(c);
        if (check) {
            this.pushBack();
        }
        return check;
    }

    @Override
    public boolean test(String s) {
        boolean check = this.check(s);
        if (check) {
            this.pushBack();
        }
        return check;
    }

    @Override
    public void pushBack() {
        if (!this.readTokens.empty()) {
            this.tokens.push(this.readTokens.pop());
        }
    }

    @Override
    public boolean canPushBack() {
        return !this.readTokens.empty();
    }

    @Override
    public String getWhitespaceBeforeToken() {
        return this.tokens.peek().getWhitespace();
    }

    @Override
    public int lineNo() {
        if (this.readTokens.empty()) {
            return 1;
        }
        return this.readTokens.peek().lineNo();
    }

    @Override
    public int nextLineNo() {
        if (this.tokens.empty()) {
            return this.lineNo();
        }
        return this.tokens.peek().lineNo();
    }

    @Override
    public void close() {
        try {
            this.tokens.empty();
            this.readTokens.empty();
            this.in.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void skipToken() {
        if (!this.tokens.empty()) {
            this.readTokens.push(this.tokens.pop());
        }
    }

    private static class CharToken
    extends Token {
        char c;

        public CharToken(char c, String whitespace, int lineNo) {
            this.c = c;
            this.whitespace = whitespace;
            this.lineNo = lineNo;
        }

        public char getChar() {
            return this.c;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + '\'' + this.c + '\'';
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.CHARACTER;
        }
    }

    private static class CommentToken
    extends Token {
        String comment;

        public CommentToken(String comment, String whitespace, int lineNo) {
            this.comment = comment;
            this.whitespace = whitespace;
            this.lineNo = lineNo;
        }

        public String getComment() {
            return this.comment;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + "//" + this.comment;
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.COMMENT;
        }
    }

    private static class EOFToken
    extends Token {
        public EOFToken(String whitespace, int lineNo) {
            this.whitespace = whitespace;
            this.lineNo = lineNo;
        }

        public String toString() {
            return this.whitespace;
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.EOF;
        }
    }

    private static class FloatToken
    extends Token {
        float f;

        public FloatToken(float fl, String whitespace, int lineNo) {
            this.lineNo = lineNo;
            this.whitespace = whitespace;
            this.f = fl;
        }

        public float getFloat() {
            return this.f;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + this.f;
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.FLOAT;
        }
    }

    private static class IntToken
    extends Token {
        int integer;

        public IntToken(int integer, String whitespace, int lineNo) {
            this.lineNo = lineNo;
            this.whitespace = whitespace;
            this.integer = integer;
        }

        public int getInt() {
            return this.integer;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + this.integer;
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.INTEGER;
        }
    }

    private static class OperatorToken
    extends Token {
        char op;

        public OperatorToken(char op, String whitespace, int lineNo) {
            this.op = op;
            this.whitespace = whitespace;
            this.lineNo = lineNo;
        }

        public char getOp() {
            return this.op;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + this.op;
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.OPERATOR;
        }
    }

    private static class StringToken
    extends Token {
        String str;

        public StringToken(String str, String whitespace, int lineNo) {
            this.str = str;
            this.whitespace = whitespace;
            this.lineNo = lineNo;
        }

        public String getString() {
            return this.str;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + '\"' + this.str + '\"';
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.STRING;
        }
    }

    private static abstract class Token {
        protected int lineNo;
        protected String whitespace;

        private Token() {
        }

        public int lineNo() {
            return this.lineNo;
        }

        public String getWhitespace() {
            return this.whitespace;
        }

        public abstract Tokenizer.TokenType getType();
    }

    public static enum TokenizerOptions {
        PROCESS_COMMENTS,
        PROCESS_STRINGS,
        PROCESS_CHARACTERS;

    }

    private static class WordToken
    extends Token {
        String word;

        public WordToken(String word, String whitespace, int lineNo) {
            this.lineNo = lineNo;
            this.whitespace = whitespace;
            this.word = word;
        }

        public String getWord() {
            return this.word;
        }

        public String toString() {
            return String.valueOf(this.whitespace) + this.word;
        }

        @Override
        public Tokenizer.TokenType getType() {
            return Tokenizer.TokenType.WORD;
        }
    }
}

