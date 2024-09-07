/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.ui.components.CellRenderer;
import edu.utexas.cs.sam.ui.components.MemoryCell;
import edu.utexas.cs.sam.utils.ProgramState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionListener;

public class SamStackPanel
extends JPanel {
    private JScrollPane stackScrollPane;
    private JPanel stackInnerPanel;
    private JList stack;

    public SamStackPanel() {
        this.setPreferredSize(new Dimension(100, 350));
        this.setMinimumSize(new Dimension(100, 100));
        this.setLayout(new BorderLayout());
        this.stack = new JList(new DefaultListModel());
        this.stack.setCellRenderer(new StackCellRenderer());
        this.stack.setSelectionMode(1);
        this.stack.setFont(new Font("Monospaced", 1, 12));
        this.stackInnerPanel = new JPanel();
        this.stackInnerPanel.setLayout(new BorderLayout());
        this.stackInnerPanel.setBackground(Color.white);
        this.stackInnerPanel.add((Component)this.stack, "South");
        this.stackScrollPane = new JScrollPane(this.stackInnerPanel);
        this.stackScrollPane.setBorder(new SoftBevelBorder(1));
        this.add((Component)new JLabel("Stack:"), "North");
        this.add((Component)this.stackScrollPane, "Center");
    }

    public void bindSelectionListener(ListSelectionListener l) {
        this.stack.addListSelectionListener(l);
    }

    public void update(Memory mem) {
        DefaultListModel stak = (DefaultListModel)this.stack.getModel();
        stak.clear();
        int addr = 0;
        for (Memory.Data item : mem.getStack()) {
            stak.add(0, new MemoryCell(item, addr++));
        }
        this.stack.ensureIndexIsVisible(0);
        this.stackScrollPane.revalidate();
        this.stackScrollPane.repaint();
    }

    public void update(ProgramState state) {
        DefaultListModel stak = (DefaultListModel)this.stack.getModel();
        stak.clear();
        int addr = 0;
        for (Memory.Data data : state.getStack()) {
            stak.add(0, new MemoryCell(data, addr++));
        }
        this.stack.ensureIndexIsVisible(0);
        this.stackScrollPane.revalidate();
        this.stackScrollPane.repaint();
    }

    private class StackCellRenderer
    extends CellRenderer
    implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                this.setBackground(list.getSelectionBackground());
            }
            MemoryCell cell = (MemoryCell)value;
            this.setText(cell.getText());
            this.setBackground(cell.getColor());
            this.setToolTipText(cell.getToolTip());
            this.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }
}

