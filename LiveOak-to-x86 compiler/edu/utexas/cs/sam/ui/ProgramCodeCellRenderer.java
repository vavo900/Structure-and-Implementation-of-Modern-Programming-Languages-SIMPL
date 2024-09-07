/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.ui.SamGUI;
import edu.utexas.cs.sam.ui.components.CellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ProgramCodeCellRenderer
extends CellRenderer
implements ListCellRenderer {
    private SamGUI.BreakpointList breakpoints;

    public ProgramCodeCellRenderer(SamGUI.BreakpointList breakpoints) {
        this.breakpoints = breakpoints;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        BreakpointIcon bp = new BreakpointIcon(Color.WHITE);
        if (this.breakpoints.checkBreakpoint(index)) {
            bp.setColor(Color.RED);
        } else if (isSelected) {
            bp.setColor(list.getSelectionBackground());
        } else if (value instanceof ProgramCodeCell && ((ProgramCodeCell)value).isExecuting()) {
            bp.setColor(new Color(204, 255, 204));
        }
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
        } else if (value instanceof ProgramCodeCell && ((ProgramCodeCell)value).isExecuting()) {
            this.setBackground(new Color(204, 255, 204));
        } else {
            this.setBackground(list.getBackground());
        }
        this.setIcon(bp);
        this.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        this.setText(value.toString());
        return this;
    }

    public void setBreakpoints(SamGUI.BreakpointList list) {
        this.breakpoints = list;
    }

    class BreakpointIcon
    implements Icon {
        private int height = 8;
        private int width = 8;
        private Color c;

        public BreakpointIcon(Color c) {
            this.c = c;
        }

        public void setColor(Color c) {
            this.c = c;
        }

        @Override
        public int getIconHeight() {
            return this.height;
        }

        @Override
        public int getIconWidth() {
            return this.width;
        }

        @Override
        public void paintIcon(Component cm, Graphics g, int x, int y) {
            g.translate(x, y);
            g.setColor(this.c);
            g.fillOval(0, 0, this.width, this.height);
            g.translate(-x, -y);
        }
    }

    public static class ProgramCodeCell {
        private int id;
        private String instruction;
        private String label;
        private boolean executing = false;

        public ProgramCodeCell(int id, String instruction, String label) {
            this.id = id;
            this.instruction = instruction;
            this.label = label;
        }

        public String toString() {
            return String.valueOf(this.id) + ": " + this.instruction + (this.label == null ? "" : "  (<= " + this.label + " )");
        }

        public boolean isExecuting() {
            return this.executing;
        }

        public void setExecuting(boolean b) {
            this.executing = b;
        }
    }
}

