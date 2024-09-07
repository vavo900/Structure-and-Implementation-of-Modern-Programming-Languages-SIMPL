/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ClassFileLoader
extends ClassLoader {
    public ClassFileLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> getClass(File f, String className) throws ClassNotFoundException {
        try {
            FileInputStream fin = new FileInputStream(f);
            byte[] b = new byte[4096];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int fileSize = 0;
            while (fin.available() > 0) {
                fileSize += fin.read(b);
                bos.write(b);
            }
            return this.defineClass(className, bos.toByteArray(), 0, fileSize);
        }
        catch (FileNotFoundException e) {
            throw new ClassNotFoundException("Class could not be found");
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Class could not be loaded (I/O error)");
        }
    }
}

