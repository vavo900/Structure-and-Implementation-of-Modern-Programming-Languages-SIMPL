/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.AssemblerException;
import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.ReferenceTable;
import edu.utexas.cs.sam.core.SamProgram;
import edu.utexas.cs.sam.core.SymbolTable;
import edu.utexas.cs.sam.core.instructions.Instruction;
import edu.utexas.cs.sam.core.instructions.SamAddressInstruction;
import edu.utexas.cs.sam.core.instructions.SamCharInstruction;
import edu.utexas.cs.sam.core.instructions.SamFloatInstruction;
import edu.utexas.cs.sam.core.instructions.SamInstruction;
import edu.utexas.cs.sam.core.instructions.SamIntInstruction;
import edu.utexas.cs.sam.core.instructions.SamStringInstruction;
import edu.utexas.cs.sam.io.SamTokenizer;
import edu.utexas.cs.sam.io.TokenParseException;
import edu.utexas.cs.sam.io.Tokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

public class SamAssembler {
    private static final String pkgName = SamInstruction.class.getPackage().getName();
    public static SamInstructionCache instructions = new SamInstructionCache();

    public static Program assemble(String filename) throws AssemblerException, FileNotFoundException, IOException {
        System.out.println(filename);
        return SamAssembler.assemble(new BufferedReader(new FileReader(filename)));
    }

    public static Program assemble(Reader r) throws AssemblerException, IOException {
        Program prog = new SamProgram();
        if ((prog = SamAssembler.parse(r, prog)).getLength() == 0) {
            throw new AssemblerException("Cannot assemble null program.");
        }
        prog.resolveReferences();
        return prog;
    }

    public static Program assemble(String[] filenames) throws AssemblerException, FileNotFoundException, IOException {
        Program prog = new SamProgram();
        String[] stringArray = filenames;
        int n = filenames.length;
        int n2 = 0;
        while (n2 < n) {
            String fname = stringArray[n2];
            BufferedReader reader = new BufferedReader(new FileReader(fname));
            prog = SamAssembler.parse(reader, prog);
            ++n2;
        }
        if (prog.getLength() == 0) {
            throw new AssemblerException("Cannot assemble null program.");
        }
        prog.resolveReferences();
        return prog;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static Program parse(Reader input, Program prog) throws AssemblerException, IOException {
        SamTokenizer in;
        try {
            in = new SamTokenizer(input, SamTokenizer.TokenizerOptions.PROCESS_STRINGS, SamTokenizer.TokenizerOptions.PROCESS_CHARACTERS);
        }
        catch (TokenParseException e) {
            throw new AssemblerException("Unable to create token stack: " + e.getMessage(), e.getLine());
        }
        SymbolTable ST = prog.getSymbolTable();
        ReferenceTable RT = prog.getReferenceTable();
        do {
            Instruction i;
            String label;
            String str = null;
            while ((label = SamAssembler.extractLabel(in)) != null) {
                if (ST.resolveAddress(label) >= 0) {
                    throw new AssemblerException("Duplicate Label", in.lineNo());
                }
                ST.add(label, prog.getLength());
            }
            if (in.peekAtKind() != Tokenizer.TokenType.WORD) {
                throw new AssemblerException("Expected instruction", in.nextLineNo());
            }
            str = in.getWord();
            try {
                i = instructions.getInstruction(str);
            }
            catch (IllegalAccessException e) {
                throw new AssemblerException("Unknown Instruction: " + str, in.lineNo());
            }
            catch (InstantiationException e) {
                throw new AssemblerException("Unknown Instruction: " + str, in.lineNo());
            }
            catch (ClassNotFoundException e) {
                throw new AssemblerException("Unknown Instruction: " + str, in.lineNo());
            }
            i.setProgram(prog);
            if (i instanceof SamIntInstruction) {
                boolean addr_ins = i instanceof SamAddressInstruction;
                if (addr_ins && in.peekAtKind() == Tokenizer.TokenType.WORD) {
                    RT.add(in.getWord(), prog.getLength());
                } else if (addr_ins && in.peekAtKind() == Tokenizer.TokenType.STRING) {
                    RT.add(in.getString(), prog.getLength());
                } else if (in.peekAtKind() == Tokenizer.TokenType.OPERATOR) {
                    if (in.getOp() != '-' || in.peekAtKind() != Tokenizer.TokenType.INTEGER) {
                        throw new AssemblerException("Instruction " + str + " requires an integer operand", in.lineNo());
                    }
                    ((SamIntInstruction)i).setOperand(-in.getInt());
                } else {
                    if (in.peekAtKind() != Tokenizer.TokenType.INTEGER) throw new AssemblerException("Instruction " + str + " requires an integer operand", in.nextLineNo());
                    ((SamIntInstruction)i).setOperand(in.getInt());
                }
            } else if (i instanceof SamFloatInstruction) {
                if (in.peekAtKind() == Tokenizer.TokenType.FLOAT) {
                    ((SamFloatInstruction)i).setOperand(in.getFloat());
                } else {
                    if (in.peekAtKind() != Tokenizer.TokenType.OPERATOR) throw new AssemblerException("Instruction " + str + " requires a float operand", in.nextLineNo());
                    if (in.getOp() != '-' || in.peekAtKind() != Tokenizer.TokenType.FLOAT) {
                        throw new AssemblerException("Instruction " + str + " requires a float operand", in.lineNo());
                    }
                    ((SamFloatInstruction)i).setOperand(-in.getFloat());
                }
            } else if (i instanceof SamCharInstruction) {
                if (in.peekAtKind() != Tokenizer.TokenType.CHARACTER) throw new AssemblerException("Instruction " + str + " requires a character operand", in.nextLineNo());
                ((SamCharInstruction)i).setOperand(in.getCharacter());
            } else if (i instanceof SamStringInstruction) {
                if (in.peekAtKind() != Tokenizer.TokenType.STRING) throw new AssemblerException("Instruction " + str + " requires a string operand", in.nextLineNo());
                ((SamStringInstruction)i).setOperand(in.getString());
            }
            prog.addInst(i);
        } while (in.peekAtKind() != Tokenizer.TokenType.EOF);
        in.close();
        return prog;
    }

    private static String extractLabel(Tokenizer in) {
        String s = null;
        if (in.peekAtKind() == Tokenizer.TokenType.WORD) {
            s = in.getWord();
        } else if (in.peekAtKind() == Tokenizer.TokenType.STRING) {
            s = in.getString();
        } else {
            return null;
        }
        if (!in.check(':')) {
            in.pushBack();
            return null;
        }
        return s;
    }

    public static class SamInstructionCache {
        private HashMap<String, Class<? extends Instruction>> instructions = new HashMap();

        public Instruction getInstruction(String s) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
            if (this.instructions.containsKey(s)) {
                Instruction i = this.instructions.get(s).newInstance();
                return i;
            }
            Class<?> c = Class.forName(String.valueOf(pkgName) + "." + "SAM_" + s);
            this.addInstruction(s, c);
            return (Instruction)c.newInstance();
        }

        public void addInstruction(String s, Class<? extends Instruction> c) {
            this.instructions.put(s, c);
        }
    }
}

