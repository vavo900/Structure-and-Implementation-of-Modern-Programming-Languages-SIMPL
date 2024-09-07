/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegistrationSystem {
    private static Map<Integer, Map<?, ?>> registrations = new HashMap();
    private static int uid = 0;

    public static synchronized <T> void register(int classID, T obj) {
        Map<?, ?> d = registrations.get(classID);
        if (d == null) {
            d = new HashMap();
            registrations.put(classID, d);
        }
        d.put(obj, obj);
    }

    public static synchronized void unregister(int classID, Object obj) {
        Map<?, ?> d = registrations.get(classID);
        if (d != null) {
            d.remove(obj);
            if (d.size() == 0) {
                registrations.remove(classID);
            }
        }
    }

    public static synchronized void unregister(int classID) {
        registrations.remove(classID);
    }

    public static synchronized Collection<?> getElements(int classID) {
        Map<?, ?> d = registrations.get(classID);
        if (d == null) {
            return null;
        }
        return d.keySet();
    }

    public static synchronized Object getElement(int classID) {
        Map<?, ?> d = registrations.get(classID);
        if (d == null) {
            return null;
        }
        Set<?> ks = d.keySet();
        if (ks.isEmpty()) {
            return null;
        }
        return ks.iterator().next();
    }

    public static synchronized int getNextUID() {
        return uid++;
    }
}

