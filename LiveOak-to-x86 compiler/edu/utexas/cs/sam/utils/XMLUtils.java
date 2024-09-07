/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.utils;

import java.io.PrintWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLUtils {
    public static void writeXML(Node n, PrintWriter out) {
        switch (n.getNodeType()) {
            case 9: {
                Document d = (Document)n;
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                XMLUtils.writeXML(d.getDocumentElement(), out);
                break;
            }
            case 1: {
                Element e = (Element)n;
                out.print("<" + e.getNodeName());
                NamedNodeMap attrs = e.getAttributes();
                int i = 0;
                while (i < attrs.getLength()) {
                    out.print(" " + attrs.item(i).getNodeName() + "=\"");
                    XMLUtils.printXML(attrs.item(i).getNodeValue(), out, true);
                    out.print("\"");
                    ++i;
                }
                out.print(">");
                Node child = e.getFirstChild();
                while (child != null) {
                    XMLUtils.writeXML(child, out);
                    child = child.getNextSibling();
                }
                out.println("</" + e.getNodeName() + ">");
                break;
            }
            case 3: {
                XMLUtils.printXML(n.getNodeValue(), out, false);
            }
        }
        out.flush();
    }

    public static void printXML(String s, PrintWriter out, boolean isAttr) {
        if (s == null) {
            return;
        }
        int i = 0;
        while (i < s.length()) {
            XMLUtils.printXML(s.charAt(i), out, isAttr);
            ++i;
        }
    }

    public static void printXML(char c, PrintWriter out, boolean isAttr) {
        switch (c) {
            case '<': {
                out.print("&lt;");
                break;
            }
            case '>': {
                out.print("&gt;");
                break;
            }
            case '&': {
                out.print("&amp;");
                break;
            }
            case '\"': {
                out.print(isAttr ? "&lt;" : "\"");
                break;
            }
            case '\r': {
                out.print("&#xD;");
                break;
            }
            case '\n': {
                out.print("&#xA;");
                break;
            }
            case '\t': {
                out.print("&#x9;");
                break;
            }
            default: {
                out.print(c);
            }
        }
    }
}

