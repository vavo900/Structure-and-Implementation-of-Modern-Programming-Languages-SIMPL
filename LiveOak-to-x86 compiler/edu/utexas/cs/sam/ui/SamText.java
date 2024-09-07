/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.core.AssemblerException;
import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.SamAssembler;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.Video;
import edu.utexas.cs.sam.core.instructions.Instruction;
import edu.utexas.cs.sam.utils.ClassFileLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class SamText
implements Video {
    private static final String BR = System.getProperty("line.separator");
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        ArrayList<String> fnames = new ArrayList<String>();
        SamText txt = new SamText();
        Sys sys = new Sys();
        Processor cpu = sys.cpu();
        Memory mem = sys.mem();
        cpu.init();
        mem.init();
        sys.setVideo(txt);
        int il = -1;
        int tl = -1;
        boolean err = false;
        int a = 0;
        while (a < args.length) {
            block27: {
                if (args[a].equals("+tl") && args.length > a + 1) {
                    try {
                        tl = Integer.parseInt(args[++a]);
                        break block27;
                    }
                    catch (NumberFormatException e) {
                        err = true;
                        break;
                    }
                }
                if (args[a].equals("+il") && args.length > a + 1) {
                    try {
                        il = Integer.parseInt(args[++a]);
                        break block27;
                    }
                    catch (NumberFormatException e) {
                        err = true;
                        break;
                    }
                }
                if (args[a].equals("+il") || args[a].equals("+tl")) {
                    err = true;
                    break;
                }
                if (args[a].equals("-load")) {
                    String name = args[++a];
                    SamText.loadInstruction(new File(name));
                } else {
                    if (args[a].equals("-help") || args[a].equals("--help")) {
                        err = true;
                        break;
                    }
                    fnames.add(args[a]);
                }
            }
            ++a;
        }
        if (err) {
            System.err.println("Usage: java SamText <options> <file1> <file2>..." + BR + "If the options are omitted, the program runs without limits." + BR + "If the filenames are omitted, System.in is used for input. " + BR + BR + "Options: +tl <integer>: Time limit in milliseconds." + BR + "         +il <integer>: Instruction limit." + BR + "         -load: Loads the specified class file as an instruction." + BR + "         -help: Shows this help message.");
            return;
        }
        try {
            Iterator<HeapAllocator.Allocation> iter;
            HeapAllocator heap;
            boolean tlim;
            Program prg;
            if (fnames.size() != 0) {
                prg = SamAssembler.assemble(fnames.toArray(new String[fnames.size()]));
            } else {
                System.out.println("Type SAM Code, EOF to end. ");
                System.out.println("(CTRL-D on Unix, CTRL-Z on Windows)");
                System.out.println("============================");
                prg = SamAssembler.assemble(new InputStreamReader(System.in));
            }
            System.out.println("Program assembled.");
            cpu.load(prg);
            System.out.println("Program loaded. Executing.");
            System.out.println("==========================");
            boolean ilim = il >= 0;
            boolean bl = tlim = tl >= 0;
            if (!ilim && !tlim) {
                cpu.run();
            } else {
                long start = 0L;
                if (tlim) {
                    start = System.currentTimeMillis();
                }
                while (cpu.get(3) == 0) {
                    cpu.step();
                    if (ilim && il-- == 0) {
                        throw new SystemException("Program exceeded instruction limit. Terminating.");
                    }
                    if (!tlim || System.currentTimeMillis() - start <= (long)tl) continue;
                    throw new SystemException("Program exceeded time limit. Terminating.");
                }
            }
            System.out.println("Exit Status: " + mem.getMem(0));
            if (cpu.get(1) != 1) {
                System.out.println("Warning: You do not have one item remaining on the stack");
            }
            if ((heap = mem.getHeapAllocator()) != null && (iter = heap.getAllocations()).hasNext()) {
                System.out.println("Warning: Your program leaks memory");
            }
        }
        catch (AssemblerException e) {
            System.err.println("Assembler error: " + e);
            return;
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
            return;
        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e);
            return;
        }
        catch (SystemException e) {
            System.err.println("Stack machine error: " + e);
            return;
        }
        catch (Exception e) {
            System.err.println("Internal Error, please report to the SaM Development Group " + BR + e);
            e.printStackTrace(System.err);
            return;
        }
    }

    /*
     * Loose catch block
     */
    @Override
    public int readInt() {
        while (true) {
            try {
                System.out.print("Processor Input (enter integer): ");
                return Integer.parseInt(in.readLine());
            }
            catch (NumberFormatException e) {
                continue;
            }
            break;
        }
        catch (IOException e) {
            return 0;
        }
    }

    @Override
    public String readString() {
        try {
            System.out.print("Processor Input (enter string): ");
            String s = in.readLine();
            return s != null ? s : "";
        }
        catch (IOException e) {
            return "";
        }
    }

    @Override
    public char readChar() {
        try {
            System.out.print("Processor Input (enter character): ");
            String s = in.readLine();
            return s != null && s.length() > 0 ? s.charAt(0) : (char)'\u0000';
        }
        catch (IOException e) {
            return '\u0000';
        }
    }

    /*
     * Loose catch block
     */
    @Override
    public float readFloat() {
        while (true) {
            try {
                System.out.print("Processor Input (enter float): ");
                return Float.parseFloat(in.readLine());
            }
            catch (NumberFormatException e) {
                continue;
            }
            break;
        }
        catch (IOException e) {
            return 0.0f;
        }
    }

    @Override
    public void writeInt(int a) {
        System.out.println("Processor Output: " + a);
    }

    @Override
    public void writeFloat(float a) {
        System.out.println("Processor Output: " + a);
    }

    @Override
    public void writeChar(char a) {
        System.out.println("Processor Output: " + a);
    }

    @Override
    public void writeString(String a) {
        System.out.println("Processor Output: " + a);
    }

    private static void loadInstruction(File f) {
        ClassFileLoader cl = new ClassFileLoader(SamText.class.getClassLoader());
        String className = f.getName();
        if (className.indexOf(46) < 0) {
            System.err.println("Error: Could not load instruction - improper filename.");
            System.exit(1);
        }
        if (!className.startsWith("SAM_")) {
            System.err.println("Class name is missing the SAM_ prefix.");
            System.exit(1);
        }
        System.out.println("Loading Instruction...");
        className = className.substring(0, className.indexOf(46));
        String instructionName = className.substring(4);
        try {
            Class<?> c = cl.getClass(f, className);
            Instruction i = (Instruction)c.newInstance();
            SamAssembler.instructions.addInstruction(instructionName, c);
            System.out.println("Loaded Instruction " + instructionName);
        }
        catch (ClassCastException err) {
            System.err.println("Error: Class does not implement the Instruction interface.");
            System.exit(1);
        }
        catch (NoClassDefFoundError err) {
            System.err.println("Error: Could not load instruction" + BR + "Check that it is marked public, and does not belong to any package.");
            System.exit(1);
        }
        catch (ClassNotFoundException err) {
            System.err.println("Error: Could not load instruction" + BR + "Check that it is marked public, and does not belong to any package.");
            System.exit(1);
        }
        catch (InstantiationException err) {
            System.err.println("Error: Could not load instruction" + BR + "Check that it is marked public, and does not belong to any package.");
            System.exit(1);
        }
        catch (IllegalAccessException err) {
            System.err.println("Error: Could not load instruction" + BR + "Check that it is marked public, and does not belong to any package.");
            System.exit(1);
        }
    }
}

