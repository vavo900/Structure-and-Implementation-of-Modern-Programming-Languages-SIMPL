/*
 * Decompiled with CFR 0.152.
 */
import edu.utexas.cs.sam.io.SamTokenizer;
import edu.utexas.cs.sam.io.Tokenizer;
import edu.utexas.cs.sam.io.TokenizerException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.Vector;

public class LiveOakCompiler {
    static Stack<Boolean> flgStck = new Stack();
    static Map<String, Integer> symTable = new LinkedHashMap<String, Integer>();
    static Map<String, Integer> methActs = new LinkedHashMap<String, Integer>();
    static Vector<Map<String, Integer>> symVec = new Vector();
    static Vector<Map<Integer, String>> symVec2 = new Vector();
    static ArrayList<String> calledMethods = new ArrayList();
    static Vector<String> par = new Vector();
    static Map<String, String> adMem = new LinkedHashMap<String, String>();
    static Map<String, String> adReg = new LinkedHashMap<String, String>();
    static Map<String, String> rdLit = new LinkedHashMap<String, String>();
    static Map<String, String> rdVar = new LinkedHashMap<String, String>();
    static Map<String, String> adMem_2 = new LinkedHashMap<String, String>();
    static Map<String, String> adReg_2 = new LinkedHashMap<String, String>();
    static Map<String, String> rdLit_2 = new LinkedHashMap<String, String>();
    static Map<String, String> rdVar_2 = new LinkedHashMap<String, String>();
    static ArrayList<String> codeReg = new ArrayList();
    static ArrayList<String> regAlloc = new ArrayList();
    static ArrayList<String> raxInfo = new ArrayList();
    static int locVar = 2;
    static int lblCnt = 0;
    static int actCnt = 0;
    static Stack<String> lblStk = new Stack();
    static Stack<Boolean> retFlagStk = new Stack();

    static String compiler(String fileName) {
        try {
            SamTokenizer f = new SamTokenizer(fileName, new SamTokenizer.TokenizerOptions[0]);
            String pgm = LiveOakCompiler.getProgram(f);
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return ".text\n.global cmain\ncmain:\nret\n";
        }
    }

    static String getProgram(SamTokenizer f) {
        try {
            Object pgm = ".text\n.global cmain\n";
            while (f.peekAtKind() != Tokenizer.TokenType.EOF) {
                pgm = (String)pgm + LiveOakCompiler.getMethod(f);
            }
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Fatal error: could not compile program");
        }
    }

    static String getMethod(SamTokenizer f) throws TokenizerException {
        try {
            int i;
            f.getWord();
            String methodName = f.getWord();
            if (Objects.equals(methodName, "main")) {
                methodName = "cmain";
            }
            if (symTable.containsKey(methodName)) {
                throw new TokenizerException("Error: Declared method already - " + methodName);
            }
            Object pgm = methodName;
            symTable.put(methodName, symVec.size());
            symVec.addElement(new LinkedHashMap());
            symVec2.addElement(new LinkedHashMap());
            symVec.lastElement().put(methodName, 0);
            symVec2.lastElement().put(0, methodName);
            pgm = (String)pgm + ":\n";
            LiveOakCompiler.checkTok(f, '(');
            if (!f.check(')')) {
                if (calledMethods.contains(methodName)) {
                    LiveOakCompiler.getFormals_2(f, methodName);
                } else {
                    LiveOakCompiler.getFormals(f, methodName);
                }
                LiveOakCompiler.checkTok(f, ')');
            }
            methActs.put(methodName, par.size());
            for (i = 1; i <= par.size(); ++i) {
                symVec.lastElement().put(par.elementAt(par.size() - i), -i);
            }
            for (i = 1; i <= par.size(); ++i) {
                symVec2.lastElement().put(i, par.elementAt(i - 1));
            }
            pgm = (String)pgm + "pushq %rbp\nmovq %rsp, %rbp\n";
            adReg_2.clear();
            rdVar_2.clear();
            rdLit_2.clear();
            adReg_2.putAll(adReg);
            rdVar_2.putAll(rdVar);
            rdLit_2.putAll(rdLit);
            pgm = (String)pgm + LiveOakCompiler.callerPushRegs();
            pgm = (String)pgm + LiveOakCompiler.calleePushRegs();
            pgm = (String)pgm + LiveOakCompiler.getBody(f);
            par.clear();
            locVar = 2;
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect method syntax.");
        }
    }

    static ArrayList<String> getExp(SamTokenizer f) throws TokenizerException {
        try {
            codeReg.set(0, "");
            switch (f.peekAtKind()) {
                case INTEGER: {
                    int a = f.getInt();
                    String intStr = Integer.toString(a);
                    codeReg.set(2, intStr);
                    codeReg.set(3, "num");
                    if (adReg.containsKey(intStr)) {
                        codeReg.set(1, adReg.get(intStr));
                        return codeReg;
                    }
                    ArrayList<String> expRegAlloc = LiveOakCompiler.getReg();
                    String reg = expRegAlloc.get(0);
                    adReg.put(intStr, reg);
                    rdLit.put(reg, intStr);
                    codeReg.set(0, codeReg.get(0) + "movq $" + a + ", " + reg + "\n");
                    codeReg.set(1, reg);
                    return codeReg;
                }
                case OPERATOR: {
                    LiveOakCompiler.checkTok(f, '(');
                    if (f.check('-') || f.check('~')) {
                        codeReg = LiveOakCompiler.getExp(f);
                        String r1 = codeReg.get(1);
                        codeReg.set(0, codeReg.get(0) + "neg " + r1 + "\n");
                        adReg.remove(codeReg.get(2));
                        rdVar.remove(r1);
                        rdLit.remove(r1);
                        codeReg.set(3, "-");
                    } else if (f.check('!')) {
                        codeReg = LiveOakCompiler.getExp(f);
                        String r1 = codeReg.get(1);
                        codeReg.set(0, codeReg.get(0) + "not " + r1 + "\n");
                        adReg.remove(codeReg.get(2));
                        rdVar.remove(r1);
                        rdLit.remove(r1);
                        codeReg.set(3, "!");
                    } else {
                        codeReg = LiveOakCompiler.getExp(f);
                        if (f.check(')')) {
                            return codeReg;
                        }
                        if (f.check('?')) {
                            String lbl1 = LiveOakCompiler.getLbl();
                            String lbl2 = LiveOakCompiler.getLbl();
                            String tempInstr = codeReg.get(0);
                            String r1 = codeReg.get(1);
                            String tempvorl = codeReg.get(2);
                            String tempInstr2 = "cmpq $1, " + r1 + "\n";
                            String tempInstr3 = "je " + lbl1 + "\n";
                            String tempInstr4 = "jmp " + lbl2 + "\n";
                            String tempInstr5 = lbl1 + ":\n";
                            codeReg = LiveOakCompiler.getExp(f);
                            f.check(':');
                            String tempInstr6 = codeReg.get(0);
                            String r2 = codeReg.get(1);
                            String tempvorl2 = codeReg.get(2);
                            String tempInstr7 = lbl2 + ":\n";
                            codeReg = LiveOakCompiler.getExp(f);
                            String tempInstr8 = codeReg.get(0);
                            String r3 = codeReg.get(1);
                            String tempvorl3 = codeReg.get(2);
                            codeReg.set(0, tempInstr + tempInstr2 + tempInstr3 + tempInstr8 + tempInstr4 + tempInstr5 + tempInstr6 + tempInstr7);
                        } else if (f.check('+') || f.check('|')) {
                            String r1 = codeReg.get(1);
                            String tempInstr = codeReg.get(0);
                            String tempvorl = codeReg.get(2);
                            codeReg = LiveOakCompiler.getExp(f);
                            codeReg.set(0, tempInstr + codeReg.get(0));
                            String r2 = codeReg.get(1);
                            codeReg.set(3, "+");
                            if (LiveOakCompiler.isNumeric(codeReg.get(2)) && !LiveOakCompiler.isNumeric(tempvorl)) {
                                codeReg.set(0, codeReg.get(0) + "addq " + r2 + ", " + r1 + "\n");
                                adReg.remove(tempvorl);
                                if (Objects.equals(r1, r2)) {
                                    adReg.remove(codeReg.get(2));
                                }
                                rdLit.remove(r1);
                                codeReg.set(1, r1);
                                codeReg.set(2, tempvorl + " + " + codeReg.get(2));
                            } else {
                                codeReg.set(0, codeReg.get(0) + "addq " + r1 + ", " + r2 + "\n");
                                adReg.remove(codeReg.get(2));
                                if (!Objects.equals(r1, r2)) {
                                    rdVar.remove(r2);
                                }
                                rdLit.remove(r2);
                            }
                        } else if (f.check('-')) {
                            String r1 = codeReg.get(1);
                            String tempInstr = codeReg.get(0);
                            String tempvorl = codeReg.get(2);
                            codeReg = LiveOakCompiler.getExp(f);
                            codeReg.set(0, tempInstr + codeReg.get(0));
                            String r2 = codeReg.get(1);
                            codeReg.set(3, "-");
                            if (LiveOakCompiler.isNumeric(codeReg.get(2)) && !LiveOakCompiler.isNumeric(tempvorl)) {
                                codeReg.set(0, codeReg.get(0) + "subq " + r2 + ", " + r1 + "\n");
                                adReg.remove(tempvorl);
                                rdVar.remove(r1);
                                rdLit.remove(r1);
                                codeReg.set(1, r1);
                            } else {
                                codeReg.set(0, codeReg.get(0) + "subq " + r1 + ", " + r2 + "\n");
                                adReg.remove(codeReg.get(2));
                                rdVar.remove(r2);
                                rdLit.remove(r2);
                            }
                        } else if (f.check('*') || f.check('&')) {
                            String r1 = codeReg.get(1);
                            String tempInstr = codeReg.get(0);
                            String tempvorl = codeReg.get(2);
                            codeReg = LiveOakCompiler.getExp(f);
                            codeReg.set(0, tempInstr + codeReg.get(0));
                            String r2 = codeReg.get(1);
                            codeReg.set(3, "*");
                            if (LiveOakCompiler.isNumeric(codeReg.get(2)) && !LiveOakCompiler.isNumeric(tempvorl)) {
                                codeReg.set(0, codeReg.get(0) + "imulq " + r2 + ", " + r1 + "\n");
                                adReg.remove(tempvorl);
                                rdVar.remove(r1);
                                rdLit.remove(r1);
                                codeReg.set(1, r1);
                            } else {
                                codeReg.set(0, codeReg.get(0) + "imulq " + r1 + ", " + r2 + "\n");
                                adReg.remove(codeReg.get(2));
                                rdVar.remove(r2);
                                rdLit.remove(r2);
                            }
                        } else if (f.check('<')) {
                            String r1 = codeReg.get(1);
                            String tempInstr = codeReg.get(0);
                            codeReg = LiveOakCompiler.getExp(f);
                            codeReg.set(0, tempInstr + codeReg.get(0));
                            String r2 = codeReg.get(1);
                            codeReg.set(0, codeReg.get(0) + "cmpq " + r2 + ", " + r1 + "\n");
                            codeReg.set(3, "<");
                        } else if (f.check('>')) {
                            String r1 = codeReg.get(1);
                            String tempInstr = codeReg.get(0);
                            codeReg = LiveOakCompiler.getExp(f);
                            codeReg.set(0, tempInstr + codeReg.get(0));
                            String r2 = codeReg.get(1);
                            codeReg.set(0, codeReg.get(0) + "cmpq " + r2 + ", " + r1 + "\n");
                            codeReg.set(3, ">");
                        } else if (f.check('=')) {
                            String r1 = codeReg.get(1);
                            String tempInstr = codeReg.get(0);
                            codeReg = LiveOakCompiler.getExp(f);
                            codeReg.set(0, tempInstr + codeReg.get(0));
                            String r2 = codeReg.get(1);
                            codeReg.set(0, codeReg.get(0) + "cmpq " + r2 + ", " + r1 + "\n");
                            codeReg.set(3, "=");
                        } else {
                            throw new TokenizerException("Error: Incorrect expression syntax");
                        }
                    }
                    LiveOakCompiler.checkTok(f, ')');
                    return codeReg;
                }
                case WORD: {
                    String expWord;
                    switch (expWord = f.getWord()) {
                        case "true": {
                            int tr = 1;
                            String trStr = Integer.toString(tr);
                            codeReg.set(2, trStr);
                            codeReg.set(3, "true");
                            if (adReg.containsKey(trStr)) {
                                codeReg.set(1, adReg.get(trStr));
                                return codeReg;
                            }
                            ArrayList<String> expRegAlloc = LiveOakCompiler.getReg();
                            String reg = expRegAlloc.get(0);
                            adReg.put(trStr, reg);
                            rdLit.put(reg, trStr);
                            codeReg.set(0, codeReg.get(0) + "movq $" + tr + ", " + reg + "\n");
                            codeReg.set(1, reg);
                            return codeReg;
                        }
                        case "false": {
                            int fl = 0;
                            String flStr = Integer.toString(fl);
                            codeReg.set(2, flStr);
                            codeReg.set(3, "false");
                            if (adReg.containsKey(flStr)) {
                                codeReg.set(1, adReg.get(flStr));
                                return codeReg;
                            }
                            ArrayList<String> expRegAlloc = LiveOakCompiler.getReg();
                            String reg = expRegAlloc.get(0);
                            adReg.put(flStr, reg);
                            rdLit.put(reg, flStr);
                            codeReg.set(0, codeReg.get(0) + "movq $" + fl + ", " + reg + "\n");
                            codeReg.set(1, reg);
                            return codeReg;
                        }
                    }
                    if (f.check('(')) {
                        String tempParam = "";
                        if (!f.check(')')) {
                            if (symTable.containsKey(expWord)) {
                                codeReg.set(0, codeReg.get(0) + LiveOakCompiler.getActs(f, expWord));
                            } else {
                                calledMethods.add(expWord);
                                codeReg.set(0, codeReg.get(0) + LiveOakCompiler.getActs_2(f, expWord));
                            }
                            LiveOakCompiler.checkTok(f, ')');
                        }
                        adReg_2.clear();
                        rdVar_2.clear();
                        rdLit_2.clear();
                        adReg_2.putAll(adReg);
                        rdVar_2.putAll(rdVar);
                        rdLit_2.putAll(rdLit);
                        codeReg.set(0, codeReg.get(0) + LiveOakCompiler.callerPushRegs());
                        codeReg.set(0, codeReg.get(0) + LiveOakCompiler.calleePushRegs());
                        codeReg.set(0, codeReg.get(0) + "call " + expWord + "\n" + tempParam);
                        codeReg.set(1, "%rax");
                        codeReg.set(0, codeReg.get(0) + LiveOakCompiler.calleePopRegs());
                        codeReg.set(0, codeReg.get(0) + LiveOakCompiler.callerPopRegs());
                        adReg.clear();
                        adReg.putAll(adReg_2);
                        rdVar.clear();
                        rdLit.clear();
                        rdVar.putAll(rdVar_2);
                        rdLit.putAll(rdLit_2);
                        codeReg.set(3, "callMethod");
                        return codeReg;
                    }
                    if (!symVec.lastElement().containsKey(expWord)) {
                        throw new TokenizerException("Error: Variable does not exist");
                    }
                    codeReg.set(2, symVec2.lastElement().get(0) + expWord);
                    codeReg.set(3, "var");
                    if (adReg.containsKey(symVec2.lastElement().get(0) + expWord)) {
                        codeReg.set(1, adReg.get(symVec2.lastElement().get(0) + expWord));
                        return codeReg;
                    }
                    ArrayList<String> expRegAlloc = LiveOakCompiler.getReg();
                    String reg = expRegAlloc.get(0);
                    adReg.put(symVec2.lastElement().get(0) + expWord, reg);
                    rdVar.put(reg, symVec2.lastElement().get(0) + expWord);
                    if (adMem.containsKey(expWord)) {
                        codeReg.set(0, codeReg.get(0) + "movq " + adMem.get(expWord) + ", " + reg + "\n");
                    }
                    codeReg.set(1, reg);
                    return codeReg;
                }
            }
            throw new TokenizerException("Error: Incorrect expression");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Tokenizer Issue");
        }
    }

    static void getFormals(SamTokenizer f, String methodName) throws TokenizerException {
        try {
            f.getWord();
            String formName = f.getWord();
            ArrayList<String> formRegAlloc = LiveOakCompiler.getReg();
            String reg = formRegAlloc.get(0);
            adReg.put(methodName + formName, reg);
            rdVar.put(reg, formName);
            if (symVec.lastElement().containsKey(formName)) {
                throw new TokenizerException("Error: " + formName + "used more than once");
            }
            par.add(formName);
            if (f.check(',')) {
                LiveOakCompiler.getFormals(f, methodName);
            }
            return;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect formals");
        }
    }

    static void getFormals_2(SamTokenizer f, String methodName) throws TokenizerException {
        try {
            f.getWord();
            String formName = f.getWord();
            par.add(formName);
            adReg.put(formName, adReg.get(methodName + par.size()));
            rdVar.put(adReg.get(methodName + par.size()), formName);
            if (f.check(',')) {
                LiveOakCompiler.getFormals_2(f, methodName);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect formals");
        }
    }

    static String getVars(SamTokenizer f) throws TokenizerException {
        try {
            String var = f.getWord();
            if (symVec.lastElement().containsKey(var) || symTable.containsKey(var)) {
                throw new TokenizerException("Error: " + var + "used more than once");
            }
            symVec.lastElement().put(var, locVar);
            ++locVar;
            Object pgm = "";
            adReg.put(symVec2.lastElement().get(0) + var, LiveOakCompiler.getReg().get(0));
            rdVar.put(LiveOakCompiler.getReg().get(0), symVec2.lastElement().get(0) + var);
            if (f.check('=')) {
                ArrayList<String> varCodeReg = LiveOakCompiler.getExp(f);
                pgm = (String)pgm + varCodeReg.get(0);
                adReg.put(symVec2.lastElement().get(0) + var, varCodeReg.get(1));
                rdVar.put(varCodeReg.get(1), symVec2.lastElement().get(0) + var);
            }
            if (f.check(',')) {
                pgm = (String)pgm + LiveOakCompiler.getVars(f);
            }
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect variables");
        }
    }

    static String getBlk(SamTokenizer f) throws TokenizerException {
        try {
            Object pgm = "";
            Boolean rtrnFlg = false;
            while (!f.check('}')) {
                pgm = (String)pgm + LiveOakCompiler.getStmnt(f);
                Boolean tmpFlg = retFlagStk.pop();
                if (!tmpFlg.booleanValue()) continue;
                rtrnFlg = true;
            }
            if (!rtrnFlg.booleanValue()) {
                retFlagStk.push(false);
            } else {
                retFlagStk.push(true);
            }
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect block");
        }
    }

    static String getLbl() {
        Object lbl = "label";
        while (symTable.containsKey((String)lbl + Integer.toString(lblCnt))) {
            ++lblCnt;
        }
        lbl = (String)lbl + Integer.toString(lblCnt);
        ++lblCnt;
        return lbl;
    }

    static String getStmnt(SamTokenizer f) throws TokenizerException {
        try {
            Object pgm = "";
            switch (f.peekAtKind()) {
                case WORD: {
                    String stmntWord;
                    switch (stmntWord = f.getWord()) {
                        case "return": {
                            ArrayList<String> stmtCodeReg = LiveOakCompiler.getExp(f);
                            pgm = (String)pgm + stmtCodeReg.get(0);
                            LiveOakCompiler.checkTok(f, ';');
                            if (Objects.equals(stmtCodeReg.get(3), "<") || Objects.equals(stmtCodeReg.get(3), ">")) {
                                String lbl1 = LiveOakCompiler.getLbl();
                                String lbl2 = LiveOakCompiler.getLbl();
                                if (Objects.equals(stmtCodeReg.get(3), "<")) {
                                    pgm = (String)pgm + "jl " + lbl1 + "\n";
                                } else if (Objects.equals(stmtCodeReg.get(3), ">")) {
                                    pgm = (String)pgm + "jg " + lbl1 + "\n";
                                }
                                String tmpStr = "movq $1, %rax\n";
                                pgm = (String)pgm + "movq $0, %rax\n";
                                pgm = (String)pgm + "jmp " + lbl2 + "\n" + lbl1 + ":\n" + tmpStr + lbl2 + ":\n";
                            } else {
                                pgm = (String)pgm + "movq " + stmtCodeReg.get(1) + ", %rax\n";
                            }
                            pgm = (String)pgm + LiveOakCompiler.calleePopRegs();
                            pgm = (String)pgm + LiveOakCompiler.callerPopRegs();
                            adReg.clear();
                            adReg.putAll(adReg_2);
                            rdVar.clear();
                            rdLit.clear();
                            rdVar.putAll(rdVar_2);
                            rdLit.putAll(rdLit_2);
                            pgm = (String)pgm + "movq %rbp, %rsp\npopq %rbp\nret\n";
                            raxInfo.set(0, stmtCodeReg.get(2));
                            if (LiveOakCompiler.isNumeric(stmtCodeReg.get(2))) {
                                raxInfo.set(2, stmtCodeReg.get(2));
                            } else {
                                raxInfo.set(1, stmtCodeReg.get(2));
                            }
                            retFlagStk.push(true);
                            return pgm;
                        }
                        case "if": {
                            String lbl1 = LiveOakCompiler.getLbl();
                            String lbl2 = LiveOakCompiler.getLbl();
                            LiveOakCompiler.checkTok(f, '(');
                            ArrayList<String> stmtCodeReg = LiveOakCompiler.getExp(f);
                            pgm = (String)pgm + stmtCodeReg.get(0);
                            LiveOakCompiler.checkTok(f, ')');
                            if (Objects.equals(stmtCodeReg.get(3), "<")) {
                                pgm = (String)pgm + "jl " + lbl1 + "\n";
                            } else if (Objects.equals(stmtCodeReg.get(3), ">")) {
                                pgm = (String)pgm + "jg " + lbl1 + "\n";
                            } else if (Objects.equals(stmtCodeReg.get(3), "=")) {
                                pgm = (String)pgm + "je " + lbl1 + "\n";
                            }
                            String tmpStr = LiveOakCompiler.getStmnt(f);
                            Boolean rFlag1 = retFlagStk.pop();
                            LiveOakCompiler.checkTok(f, "else");
                            pgm = (String)pgm + LiveOakCompiler.getStmnt(f);
                            Boolean rFlag2 = retFlagStk.pop();
                            pgm = (String)pgm + "jmp " + lbl2 + "\n" + lbl1 + ":\n" + tmpStr + lbl2 + ":\n";
                            if (rFlag1.booleanValue() && rFlag2.booleanValue()) {
                                retFlagStk.push(true);
                            } else {
                                retFlagStk.push(false);
                            }
                            return pgm;
                        }
                        case "while": {
                            String lbl1 = LiveOakCompiler.getLbl();
                            String lbl2 = LiveOakCompiler.getLbl();
                            lblStk.push(lbl2);
                            LiveOakCompiler.checkTok(f, '(');
                            pgm = (String)pgm + lbl1 + ":\n";
                            ArrayList<String> stmtCodeReg = LiveOakCompiler.getExp(f);
                            pgm = (String)pgm + stmtCodeReg.get(0);
                            if (Objects.equals(stmtCodeReg.get(3), "<")) {
                                pgm = (String)pgm + "jge " + lbl2 + "\n";
                            } else if (Objects.equals(stmtCodeReg.get(3), ">")) {
                                pgm = (String)pgm + "jle " + lbl2 + "\n";
                            } else if (Objects.equals(stmtCodeReg.get(3), "=")) {
                                pgm = (String)pgm + "jne " + lbl2 + "\n";
                            }
                            LiveOakCompiler.checkTok(f, ')');
                            flgStck.push(true);
                            pgm = (String)pgm + LiveOakCompiler.getStmnt(f);
                            pgm = (String)pgm + "jmp " + lbl1 + "\n" + lbl2 + ":\n";
                            lblStk.pop();
                            flgStck.pop();
                            return pgm;
                        }
                        case "break": {
                            if (flgStck.empty()) {
                                throw new TokenizerException("Error: Incorrect break");
                            }
                            LiveOakCompiler.checkTok(f, ';');
                            retFlagStk.push(false);
                            return "jmp " + lblStk.peek() + "\n";
                        }
                    }
                    LiveOakCompiler.checkTok(f, '=');
                    ArrayList<String> stmtCodeReg = LiveOakCompiler.getExp(f);
                    pgm = (String)pgm + stmtCodeReg.get(0);
                    pgm = (String)pgm + "movq " + stmtCodeReg.get(1) + ", " + adReg.get(symVec2.lastElement().get(0) + stmntWord) + "\n";
                    LiveOakCompiler.checkTok(f, ';');
                    rdVar.put(stmtCodeReg.get(1), symVec2.lastElement().get(0) + stmntWord);
                    adReg.put(symVec2.lastElement().get(0) + stmntWord, stmtCodeReg.get(1));
                    retFlagStk.push(false);
                    return pgm;
                }
                case OPERATOR: {
                    switch (f.getOp()) {
                        case '{': {
                            pgm = (String)pgm + LiveOakCompiler.getBlk(f);
                            return pgm;
                        }
                        case ';': {
                            retFlagStk.push(false);
                            return pgm;
                        }
                    }
                    throw new TokenizerException("Error: Incorrect Operator");
                }
            }
            throw new TokenizerException("Error: Incorrect Statement");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect Statement");
        }
    }

    static String getBody(SamTokenizer f) throws TokenizerException {
        try {
            Object pgm = "";
            Boolean rFlag = false;
            LiveOakCompiler.checkTok(f, '{');
            switch (f.peekAtKind()) {
                case WORD: {
                    while (f.check("int")) {
                        pgm = (String)pgm + LiveOakCompiler.getVars(f);
                        LiveOakCompiler.checkTok(f, ';');
                    }
                    while (!f.check('}')) {
                        pgm = (String)pgm + LiveOakCompiler.getStmnt(f);
                        Boolean tmpFlag = retFlagStk.pop();
                        if (!tmpFlag.booleanValue()) continue;
                        rFlag = true;
                    }
                    if (!rFlag.booleanValue()) {
                        throw new TokenizerException("Error: Did not return");
                    }
                    return pgm;
                }
                case OPERATOR: {
                    while (!f.check('}')) {
                        pgm = (String)pgm + LiveOakCompiler.getStmnt(f);
                        Boolean tmpFlag = retFlagStk.pop();
                        if (!tmpFlag.booleanValue()) continue;
                        rFlag = true;
                    }
                    if (!rFlag.booleanValue()) {
                        throw new TokenizerException("Error: Did not return");
                    }
                    return pgm;
                }
            }
            throw new TokenizerException("Error: Incorrect body");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect body");
        }
    }

    static String getActs(SamTokenizer f, String methodNm) throws TokenizerException {
        try {
            int actCntr = 0;
            Object pgm = "";
            ArrayList<String> actCodeReg = LiveOakCompiler.getExp(f);
            pgm = (String)pgm + actCodeReg.get(0);
            String actTemp = symVec2.elementAt(symTable.get(methodNm)).get(++actCntr);
            String actRegTemp = adReg.get(methodNm + actTemp);
            pgm = (String)pgm + "movq " + actCodeReg.get(1) + ", " + actRegTemp + "\n";
            adReg.put(methodNm + actCodeReg.get(2), actRegTemp);
            if (LiveOakCompiler.isNumeric(actCodeReg.get(2))) {
                rdLit.put(actRegTemp, actCodeReg.get(2));
            } else {
                rdVar.put(actRegTemp, methodNm + actCodeReg.get(2));
            }
            while (f.check(',')) {
                actCodeReg = LiveOakCompiler.getExp(f);
                pgm = (String)pgm + actCodeReg.get(0);
                actTemp = symVec2.elementAt(symTable.get(methodNm)).get(++actCntr);
                actRegTemp = adReg.get(methodNm + actTemp);
                pgm = (String)pgm + "movq " + actCodeReg.get(1) + ", " + actRegTemp + "\n";
                adReg.put(methodNm + actCodeReg.get(2), actRegTemp);
                if (LiveOakCompiler.isNumeric(actCodeReg.get(2))) {
                    rdLit.put(actRegTemp, actCodeReg.get(2));
                    continue;
                }
                rdVar.put(actRegTemp, methodNm + actCodeReg.get(2));
            }
            if (actCntr != methActs.get(methodNm)) {
                throw new TokenizerException("Error: Incorrect actuals");
            }
            actCnt = actCntr;
            actCntr = 0;
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect actuals");
        }
    }

    static String getActs_2(SamTokenizer f, String methodNm) throws TokenizerException {
        try {
            int actCntr = 0;
            Object pgm = "";
            ArrayList<String> actCodeReg = LiveOakCompiler.getExp(f);
            pgm = (String)pgm + actCodeReg.get(0);
            adReg.put(methodNm + ++actCntr, actCodeReg.get(1));
            rdVar.put(actCodeReg.get(1), methodNm + actCntr);
            while (f.check(',')) {
                actCodeReg = LiveOakCompiler.getExp(f);
                pgm = (String)pgm + actCodeReg.get(0);
                adReg.put(methodNm + ++actCntr, actCodeReg.get(1));
                rdVar.put(actCodeReg.get(1), methodNm + actCntr);
            }
            actCnt = actCntr;
            actCntr = 0;
            return pgm;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenizerException("Error: Incorrect actuals");
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    static void checkTok(SamTokenizer f, char tok) throws TokenizerException {
        if (!f.check(tok)) {
            throw new TokenizerException("Error: Expected " + tok);
        }
    }

    static void checkTok(SamTokenizer f, String tokStr) throws TokenizerException {
        if (!f.check(tokStr)) {
            throw new TokenizerException("Error: Expected " + tokStr);
        }
    }

    static ArrayList<String> getReg() {
        regAlloc.set(1, "false");
        if (!rdVar.containsKey("%rbx") && !rdLit.containsKey("%rbx")) {
            regAlloc.set(0, "%rbx");
        } else if (!rdVar.containsKey("%rcx") && !rdLit.containsKey("%rcx")) {
            regAlloc.set(0, "%rcx");
        } else if (!rdVar.containsKey("%rdx") && !rdLit.containsKey("%rdx")) {
            regAlloc.set(0, "%rdx");
        } else if (!rdVar.containsKey("%rsi") && !rdLit.containsKey("%rsi")) {
            regAlloc.set(0, "%rsi");
        } else if (!rdVar.containsKey("%rdi") && !rdLit.containsKey("%rdi")) {
            regAlloc.set(0, "%rdi");
        } else if (!rdVar.containsKey("%r8") && !rdLit.containsKey("%r8")) {
            regAlloc.set(0, "%r8");
        } else if (!rdVar.containsKey("%r9") && !rdLit.containsKey("%r9")) {
            regAlloc.set(0, "%r9");
        } else if (!rdVar.containsKey("%r10") && !rdLit.containsKey("%r10")) {
            regAlloc.set(0, "%r10");
        } else if (!rdVar.containsKey("%r11") && !rdLit.containsKey("%r11")) {
            regAlloc.set(0, "%r11");
        } else if (!rdVar.containsKey("%r12") && !rdLit.containsKey("%r12")) {
            regAlloc.set(0, "%r12");
        } else if (!rdVar.containsKey("%r13") && !rdLit.containsKey("%r13")) {
            regAlloc.set(0, "%r13");
        } else if (!rdVar.containsKey("%r14") && !rdLit.containsKey("%r14")) {
            regAlloc.set(0, "%r14");
        } else if (!rdVar.containsKey("%r15") && !rdLit.containsKey("%r15")) {
            regAlloc.set(0, "%r15");
        } else {
            regAlloc.set(0, "%r15");
            regAlloc.set(1, "true");
        }
        return regAlloc;
    }

    static String callerPushRegs() {
        return "push %rbx\npush %r12\npush %r13\npush %r14\npush %r15\n";
    }

    static String callerPopRegs() {
        return "pop %r15\npop %r14\npop %r13\npop %r12\npop %rbx\n";
    }

    static String calleePushRegs() {
        return "push %rcx\npush %rdx\npush %rsi\npush %rdi\npush %r8\npush %r9\npush %r10\npush %r11\n";
    }

    static String calleePopRegs() {
        return "pop %r11\npop %r10\npop %r9\npop %r8\npop %rdi\npop %rsi\npop %rdx\npop %rcx\n";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        codeReg.add("");
        codeReg.add("");
        codeReg.add("");
        codeReg.add("");
        regAlloc.add("");
        regAlloc.add("");
        raxInfo.add("");
        raxInfo.add("");
        raxInfo.add("");
        String inFile = args[0];
        String pgm = LiveOakCompiler.compiler(inFile);
        String outFile = args[1];
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(outFile), "utf-8"));
            writer.write(pgm);
        }
        catch (IOException iOException) {
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception exception) {}
        }
    }
}

