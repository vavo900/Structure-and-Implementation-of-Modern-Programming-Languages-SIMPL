/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import edu.utexas.cs.sam.core.Memory;
import java.awt.Color;

public class MemoryCell {
    public static final Color COLOR_INT = Color.WHITE;
    public static final Color COLOR_FLOAT = new Color(255, 255, 204);
    public static final Color COLOR_MA = new Color(255, 204, 204);
    public static final Color COLOR_PA = new Color(204, 255, 204);
    public static final Color COLOR_CH = new Color(220, 204, 255);
    public static final Color COLOR_DEFAULT = Color.WHITE;
    private Memory.Data data;
    private int address;
    private String text;
    private Color color;
    private String tooltip_text;

    public MemoryCell(Memory.Data data, int address) {
        this.data = data;
        this.address = address;
        this.text = String.valueOf(address) + ": ";
        this.tooltip_text = "<html> Address: " + address + " <br> Type: ";
        switch (data.getType()) {
            case INT: {
                this.text = String.valueOf(this.text) + "I : " + data;
                this.tooltip_text = String.valueOf(this.tooltip_text) + "Integer";
                this.color = COLOR_INT;
                break;
            }
            case FLOAT: {
                this.text = String.valueOf(this.text) + "F : " + data;
                this.color = COLOR_FLOAT;
                this.tooltip_text = String.valueOf(this.tooltip_text) + "Floting Point";
                break;
            }
            case MA: {
                this.text = String.valueOf(this.text) + "M : " + data;
                this.color = COLOR_MA;
                this.tooltip_text = String.valueOf(this.tooltip_text) + "Memory Address";
                break;
            }
            case PA: {
                this.text = String.valueOf(this.text) + "P : " + data;
                this.color = COLOR_PA;
                this.tooltip_text = String.valueOf(this.tooltip_text) + "Program Address";
                break;
            }
            case CH: {
                this.text = String.valueOf(this.text) + "C : " + data;
                this.color = COLOR_CH;
                this.tooltip_text = String.valueOf(this.tooltip_text) + "Character";
                break;
            }
            default: {
                this.text = String.valueOf(this.text) + data;
                this.color = COLOR_DEFAULT;
                this.tooltip_text = String.valueOf(this.tooltip_text) + "Unknown";
            }
        }
    }

    public String getText() {
        return this.text;
    }

    public String getToolTip() {
        return this.tooltip_text;
    }

    public Color getColor() {
        return this.color;
    }
}

