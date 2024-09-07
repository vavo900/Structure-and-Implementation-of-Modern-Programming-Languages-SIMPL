/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import java.util.List;

public interface Memory {
    public static final int MEMORYLIMIT = 10000;
    public static final int STACKLIMIT = 1000;
    public static final int UNIT_SIZE = 32;

    public void init();

    public Sys getSystem();

    public void setSystem(Sys var1);

    public void setHeapAllocator(HeapAllocator var1);

    public HeapAllocator getHeapAllocator();

    public void setMem(int var1, Data var2) throws SystemException;

    public void setMem(int var1, int var2, Type var3) throws SystemException;

    public void setValue(int var1, int var2) throws SystemException;

    public void setType(int var1, Type var2) throws SystemException;

    public Data getMem(int var1) throws SystemException;

    public int getValue(int var1) throws SystemException;

    public Type getType(int var1) throws SystemException;

    public List<Data> getAllocation(HeapAllocator.Allocation var1);

    public List<Data> getStack();

    public void push(Data var1) throws SystemException;

    public void push(int var1, Type var2) throws SystemException;

    public void pushINT(int var1) throws SystemException;

    public void pushCH(char var1) throws SystemException;

    public void pushMA(int var1) throws SystemException;

    public void pushPA(int var1) throws SystemException;

    public void pushFLOAT(float var1) throws SystemException;

    public Data pop() throws SystemException;

    public int popValue() throws SystemException;

    public int popINT() throws SystemException;

    public char popCH() throws SystemException;

    public int popMA() throws SystemException;

    public int popPA() throws SystemException;

    public float popFLOAT() throws SystemException;

    public static class Data {
        private int value;
        private Type type;

        public Data(int value, Type type) {
            this.value = value;
            this.type = type;
        }

        public int getValue() {
            return this.value;
        }

        public Type getType() {
            return this.type;
        }

        public String toString() {
            String ret = "";
            switch (this.type) {
                case MA: 
                case INT: 
                case PA: {
                    ret = String.valueOf(ret) + this.value;
                    break;
                }
                case CH: {
                    ret = String.valueOf(ret) + "'" + (char)this.value + "'";
                    break;
                }
                case FLOAT: {
                    ret = String.valueOf(ret) + Float.intBitsToFloat(this.value);
                    break;
                }
                default: {
                    ret = String.valueOf(ret) + this.getValue();
                }
            }
            return ret;
        }
    }

    public static enum Type {
        MA,
        INT,
        FLOAT,
        PA,
        CH;


        public int toInt() {
            return this.ordinal();
        }

        public static Type fromInt(int code) {
            switch (code) {
                case 0: {
                    return MA;
                }
                case 1: {
                    return INT;
                }
                case 2: {
                    return FLOAT;
                }
                case 3: {
                    return PA;
                }
                case 4: {
                    return CH;
                }
            }
            return INT;
        }
    }
}

