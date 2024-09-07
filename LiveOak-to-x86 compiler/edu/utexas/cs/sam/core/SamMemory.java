/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import java.util.LinkedList;
import java.util.List;

public class SamMemory
implements Memory {
    private static final int INTERNALLIMIT = 20000;
    private int[] memory = new int[20000];
    private Sys sys = null;
    private Processor cpu = null;
    private HeapAllocator heap = null;

    public SamMemory(Sys sys) {
        this.setSystem(sys);
    }

    @Override
    public Sys getSystem() {
        return this.sys;
    }

    @Override
    public void setSystem(Sys sys) {
        this.sys = sys;
        this.cpu = sys.cpu();
    }

    @Override
    public HeapAllocator getHeapAllocator() {
        return this.heap;
    }

    @Override
    public void setHeapAllocator(HeapAllocator heap) {
        this.heap = heap;
    }

    private void checkAddress(int pos) throws SystemException {
        if (pos < 0 || pos > 9999) {
            throw new SystemException("Invalid memory address: " + pos);
        }
    }

    @Override
    public void init() {
        int i = 0;
        while (i < 20000) {
            this.memory[i] = 0;
            ++i;
        }
        if (this.heap != null) {
            this.heap.init();
        }
    }

    @Override
    public Memory.Data getMem(int pos) throws SystemException {
        this.checkAddress(pos);
        return new Memory.Data(this.memory[2 * pos + 1], Memory.Type.fromInt(this.memory[2 * pos]));
    }

    @Override
    public int getValue(int pos) throws SystemException {
        this.checkAddress(pos);
        return this.memory[2 * pos + 1];
    }

    @Override
    public Memory.Type getType(int pos) throws SystemException {
        this.checkAddress(pos);
        return Memory.Type.fromInt(this.memory[2 * pos]);
    }

    @Override
    public void setMem(int pos, Memory.Data data) throws SystemException {
        this.checkAddress(pos);
        this.memory[2 * pos] = data.getType().toInt();
        this.memory[2 * pos + 1] = data.getValue();
    }

    @Override
    public void setMem(int pos, int data, Memory.Type type) throws SystemException {
        this.checkAddress(pos);
        this.memory[2 * pos] = type.toInt();
        this.memory[2 * pos + 1] = data;
    }

    @Override
    public void setValue(int pos, int data) throws SystemException {
        this.checkAddress(pos);
        this.memory[2 * pos + 1] = data;
    }

    @Override
    public void setType(int pos, Memory.Type type) throws SystemException {
        this.checkAddress(pos);
        this.memory[2 * pos] = type.toInt();
    }

    @Override
    public List<Memory.Data> getAllocation(HeapAllocator.Allocation alloc) {
        LinkedList<Memory.Data> list = new LinkedList<Memory.Data>();
        int limit = alloc.getAddr() + alloc.getSize();
        int a = alloc.getAddr();
        while (a < limit) {
            list.add(new Memory.Data(this.memory[a * 2 + 1], Memory.Type.fromInt(this.memory[a * 2])));
            ++a;
        }
        return list;
    }

    @Override
    public List<Memory.Data> getStack() {
        LinkedList<Memory.Data> list = new LinkedList<Memory.Data>();
        int limit = this.cpu.get(1);
        int a = 0;
        while (a < limit) {
            list.add(new Memory.Data(this.memory[a * 2 + 1], Memory.Type.fromInt(this.memory[a * 2])));
            ++a;
        }
        return list;
    }

    @Override
    public Memory.Data pop() throws SystemException {
        return this.getMem(this.cpu.dec(1));
    }

    @Override
    public int popValue() throws SystemException {
        return this.getValue(this.cpu.dec(1));
    }

    @Override
    public void push(Memory.Data data) throws SystemException {
        this.cpu.verify(1, this.cpu.get(1) + 1);
        this.setMem(this.cpu.get(1), data);
        this.cpu.inc(1);
    }

    @Override
    public void push(int value, Memory.Type type) throws SystemException {
        this.cpu.verify(1, this.cpu.get(1) + 1);
        this.setMem(this.cpu.get(1), value, type);
        this.cpu.inc(1);
    }

    @Override
    public float popFLOAT() throws SystemException {
        return Float.intBitsToFloat(this.popValue());
    }

    @Override
    public void pushFLOAT(float fl) throws SystemException {
        this.push(Float.floatToIntBits(fl), Memory.Type.FLOAT);
    }

    @Override
    public int popINT() throws SystemException {
        return this.popValue();
    }

    @Override
    public void pushINT(int i) throws SystemException {
        this.push(i, Memory.Type.INT);
    }

    @Override
    public char popCH() throws SystemException {
        return (char)this.popValue();
    }

    @Override
    public void pushCH(char ch) throws SystemException {
        this.push(ch, Memory.Type.CH);
    }

    @Override
    public int popPA() throws SystemException {
        return this.popValue();
    }

    @Override
    public void pushPA(int pa) throws SystemException {
        this.push(pa, Memory.Type.PA);
    }

    @Override
    public int popMA() throws SystemException {
        return this.popValue();
    }

    @Override
    public void pushMA(int ma) throws SystemException {
        this.push(ma, Memory.Type.MA);
    }
}

