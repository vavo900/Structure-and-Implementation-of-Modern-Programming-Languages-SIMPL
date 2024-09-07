/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.ui.components.GridBagUtils;
import edu.utexas.cs.sam.utils.ProgramState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class SamRegistersPanel
extends JPanel {
    private JLabel pcRegister;
    private JLabel fbrRegister;
    private JLabel spRegister;
    private JLabel hpRegister;
    private JPanel registersInnerPanel;

    public SamRegistersPanel() {
        this.setPreferredSize(new Dimension(100, 150));
        this.setLayout(new BorderLayout());
        this.registersInnerPanel = new JPanel();
        this.registersInnerPanel.setBorder(new SoftBevelBorder(1));
        GridBagLayout lr = new GridBagLayout();
        GridBagConstraints cr = new GridBagConstraints();
        cr.fill = 1;
        cr.insets = new Insets(5, 5, 5, 5);
        this.registersInnerPanel.setLayout(lr);
        this.registersInnerPanel.setBackground(new Color(220, 220, 220));
        cr.anchor = 17;
        GridBagUtils.addComponent(new JLabel("PC:"), this.registersInnerPanel, lr, cr, 0, 0, 1, 1, 1.0, 1.0);
        GridBagUtils.addComponent(new JLabel("FBR:"), this.registersInnerPanel, lr, cr, 0, 1, 1, 1, 1.0, 1.0);
        GridBagUtils.addComponent(new JLabel("SP:"), this.registersInnerPanel, lr, cr, 0, 2, 1, 1, 1.0, 1.0);
        this.pcRegister = new JLabel("");
        this.fbrRegister = new JLabel("");
        this.spRegister = new JLabel("");
        this.hpRegister = new JLabel("");
        cr.anchor = 13;
        GridBagUtils.addComponent(this.pcRegister, this.registersInnerPanel, lr, cr, 1, 0, 1, 1, 1.0, 1.0);
        GridBagUtils.addComponent(this.fbrRegister, this.registersInnerPanel, lr, cr, 1, 1, 1, 1, 1.0, 1.0);
        GridBagUtils.addComponent(this.spRegister, this.registersInnerPanel, lr, cr, 1, 2, 1, 1, 1.0, 1.0);
        GridBagUtils.addComponent(this.hpRegister, this.registersInnerPanel, lr, cr, 1, 3, 1, 1, 1.0, 1.0);
        this.add((Component)new JLabel("Registers:"), "North");
        this.add((Component)this.registersInnerPanel, "Center");
    }

    public void update(Processor proc) {
        this.pcRegister.setText("" + proc.get(0));
        this.fbrRegister.setText("" + proc.get(2));
        this.spRegister.setText("" + proc.get(1));
    }

    public void update(ProgramState state) {
        this.pcRegister.setText("" + state.getRegisters()[0]);
        this.fbrRegister.setText("" + state.getRegisters()[2]);
        this.spRegister.setText("" + state.getRegisters()[1]);
    }
}

