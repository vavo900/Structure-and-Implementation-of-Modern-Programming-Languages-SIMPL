/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SamAboutDialog
extends JDialog {
    public SamAboutDialog(String packageName, String packageVersion, String component, JFrame parent) {
        super((Frame)parent, true);
        Container p = this.getContentPane();
        this.setTitle("About");
        this.setSize(300, 300);
        p.setLayout(new BorderLayout());
        p.add((Component)new JLabel("<html><body><table><tr><td><font size=\"5\">" + packageName + " v" + packageVersion + "</font>" + "</td></tr>" + "<tr><td>" + component + "</td></tr>" + "</table>" + "<table><tr>" + "<td>Programmers:<br>" + "<i> Ivan Gyurdiev </i><br>" + "<i> David Levitan </i><br>" + "</td>" + "<td> Original Design:<br>" + "<i>Professor K. Pingali </i><br>" + "<i>Professor D. Schwartz</i>" + "</td>" + "</tr></table>" + "</body></html>"), "Center");
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamAboutDialog.this.setVisible(false);
            }
        });
        p.add((Component)closeButton, "South");
        this.pack();
    }
}

