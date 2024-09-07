/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import edu.utexas.cs.sam.ui.components.GridBagUtils;
import edu.utexas.cs.sam.ui.components.MemoryCell;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SamColorReferenceDialog
extends JDialog {
    public SamColorReferenceDialog(JFrame parent) {
        super((Frame)parent, false);
        JPanel colorPanel = new JPanel();
        Container p = this.getContentPane();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 0;
        c.anchor = 10;
        c.insets = new Insets(5, 5, 5, 5);
        GridBagLayout l = new GridBagLayout();
        colorPanel.setLayout(l);
        this.setTitle("Stack Color Reference");
        this.setSize(300, 300);
        p.setLayout(new BorderLayout());
        p.add((Component)new JLabel("Stack Colors:"), "North");
        colorPanel.setLayout(l);
        c.anchor = 13;
        GridBagUtils.addComponent(new JLabel("Integers:"), colorPanel, l, c, 0, 0, 1, 1, 1.0, 1.0);
        c.anchor = 10;
        GridBagUtils.addComponent(this.createColorPanel(MemoryCell.COLOR_INT), colorPanel, l, c, 1, 0, 1, 1, 1.0, 1.0);
        c.anchor = 13;
        GridBagUtils.addComponent(new JLabel("Floats:"), colorPanel, l, c, 0, 1, 1, 1, 1.0, 1.0);
        c.anchor = 10;
        GridBagUtils.addComponent(this.createColorPanel(MemoryCell.COLOR_FLOAT), colorPanel, l, c, 1, 1, 1, 1, 1.0, 1.0);
        c.anchor = 13;
        GridBagUtils.addComponent(new JLabel("Memory Addresses:"), colorPanel, l, c, 0, 2, 1, 1, 1.0, 1.0);
        c.anchor = 10;
        GridBagUtils.addComponent(this.createColorPanel(MemoryCell.COLOR_MA), colorPanel, l, c, 1, 2, 1, 1, 1.0, 1.0);
        c.anchor = 13;
        GridBagUtils.addComponent(new JLabel("Program Addresses:"), colorPanel, l, c, 0, 3, 1, 1, 1.0, 1.0);
        c.anchor = 10;
        GridBagUtils.addComponent(this.createColorPanel(MemoryCell.COLOR_PA), colorPanel, l, c, 1, 3, 1, 1, 1.0, 1.0);
        c.anchor = 13;
        GridBagUtils.addComponent(new JLabel("Characters:"), colorPanel, l, c, 0, 4, 1, 1, 1.0, 1.0);
        c.anchor = 10;
        GridBagUtils.addComponent(this.createColorPanel(MemoryCell.COLOR_CH), colorPanel, l, c, 1, 4, 1, 1, 1.0, 1.0);
        p.add((Component)colorPanel, "Center");
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamColorReferenceDialog.this.setVisible(false);
            }
        });
        p.add((Component)closeButton, "South");
        this.pack();
    }

    private JPanel createColorPanel(Color c) {
        JPanel p = new JPanel();
        p.add(new JLabel("        "));
        p.setBackground(c);
        p.setMinimumSize(new Dimension(90, 15));
        return p;
    }
}

