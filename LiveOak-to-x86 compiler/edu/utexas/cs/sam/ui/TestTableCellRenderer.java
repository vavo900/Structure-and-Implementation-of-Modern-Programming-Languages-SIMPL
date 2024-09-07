/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.ui.TestScript;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class TestTableCellRenderer
extends JLabel
implements TableCellRenderer {
    protected TestScript tests;

    public TestTableCellRenderer() {
        this.setOpaque(true);
    }

    public void setTestScript(TestScript tests) {
        this.tests = tests;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TestScript.Test t;
        boolean error = false;
        if (this.tests != null && (t = this.tests.getTests().get(row)).isCompleted()) {
            error = t.error();
        }
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }
        if (error) {
            this.setForeground(Color.RED);
        } else {
            this.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        }
        this.setText(value.toString());
        this.setFont(table.getFont());
        if (column == 0) {
            this.setHorizontalAlignment(10);
        } else {
            this.setHorizontalAlignment(0);
        }
        return this;
    }
}

