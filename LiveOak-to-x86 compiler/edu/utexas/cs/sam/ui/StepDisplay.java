/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.ui.components.SamRegistersPanel;
import edu.utexas.cs.sam.ui.components.SamStackPanel;
import edu.utexas.cs.sam.utils.ProgramState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class StepDisplay
extends JPanel {
    protected JLabel instructionLabel;
    protected SamStackPanel stack;
    protected SamRegistersPanel registers;

    public StepDisplay() {
        this.setPreferredSize(new Dimension(150, 450));
        this.setMinimumSize(new Dimension(150, 300));
        this.setLayout(new BorderLayout());
        this.setBorder(new TitledBorder(new EtchedBorder(1), ""));
        this.stack = new SamStackPanel();
        this.stack.bindSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ((JList)e.getSource()).clearSelection();
            }
        });
        this.add((Component)this.stack, "Center");
        this.registers = new SamRegistersPanel();
        this.add((Component)this.registers, "South");
    }

    public void setCurrent(boolean active) {
        Color defaultTextColor = Color.BLACK;
        Color defaultBorderColor = this.getBackground();
        Color currentColor = Color.BLUE;
        if (active) {
            ((TitledBorder)this.getBorder()).setTitleColor(currentColor);
            ((TitledBorder)this.getBorder()).setBorder(new EtchedBorder(1, currentColor.brighter(), currentColor.darker()));
        } else {
            ((TitledBorder)this.getBorder()).setTitleColor(defaultTextColor);
            ((TitledBorder)this.getBorder()).setBorder(new EtchedBorder(1, defaultBorderColor.brighter(), defaultBorderColor.darker()));
        }
    }

    public void load(ProgramState state, Program program) {
        this.setBorder(new TitledBorder(new EtchedBorder(1), "After " + program.getInst(state.getLastPC()).toString()));
        this.stack.update(state);
        this.registers.update(state);
    }
}

