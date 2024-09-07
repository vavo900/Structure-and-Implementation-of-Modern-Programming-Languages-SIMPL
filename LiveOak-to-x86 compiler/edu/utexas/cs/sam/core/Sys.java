/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.core;

import edu.utexas.cs.sam.core.ExplicitFreeAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.SamMemory;
import edu.utexas.cs.sam.core.SamProcessor;
import edu.utexas.cs.sam.core.Video;
import edu.utexas.cs.sam.utils.RegistrationSystem;
import java.util.Collection;

public class Sys {
    public static final String SAM_VERSION = "2.6.2";
    private int procID = RegistrationSystem.getNextUID();
    private int memID = RegistrationSystem.getNextUID();
    private int vidID = RegistrationSystem.getNextUID();

    public Processor cpu() {
        return (Processor)RegistrationSystem.getElement(this.procID);
    }

    public Collection<Processor> cpus() {
        return RegistrationSystem.getElements(this.procID);
    }

    public Memory mem() {
        return (Memory)RegistrationSystem.getElement(this.memID);
    }

    public Video video() {
        return (Video)RegistrationSystem.getElement(this.vidID);
    }

    public void setVideo(Video v) {
        RegistrationSystem.register(this.vidID, v);
    }

    public Sys() {
        this(1);
    }

    public Sys(int n) {
        int i = 0;
        while (i < n) {
            RegistrationSystem.register(this.procID, new SamProcessor(this));
            ++i;
        }
        SamMemory mem = new SamMemory(this);
        mem.setHeapAllocator(new ExplicitFreeAllocator());
        mem.getHeapAllocator().setMemory(mem);
        RegistrationSystem.register(this.memID, mem);
    }
}

