/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.ui.SamCapture;
import edu.utexas.cs.sam.ui.SamGUI;
import edu.utexas.cs.sam.ui.SamTester;
import edu.utexas.cs.sam.ui.components.SamAboutDialog;
import edu.utexas.cs.sam.utils.RegistrationSystem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public abstract class SamUI {
    public static void exit() {
        int[] targets;
        int[] nArray = targets = new int[]{SamGUI.classID, SamCapture.classID, SamTester.classID};
        int n = targets.length;
        int n2 = 0;
        while (n2 < n) {
            int id = nArray[n2];
            Collection<?> cl = RegistrationSystem.getElements(id);
            if (cl != null) {
                for (Object element : cl) {
                    if (((Component)element).close()) continue;
                    return;
                }
            }
            ++n2;
        }
        System.exit(0);
    }

    private static void printUsage(String caller) {
        if (caller.equals("SamGUI")) {
            System.out.print("SaM Simulator");
        } else if (caller.equals("SamCapture")) {
            System.out.print("SaM Capture Viewer");
        } else if (caller.equals("SamTester")) {
            System.out.print("SaM Tester");
        }
        System.out.println(" (SaM 2.6.2)\n");
        System.out.println("Usage:");
        System.out.println("java ui." + caller + " [-<program> [<filename>]]");
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        catch (ClassNotFoundException e) {
            System.err.println("Unable to initialize look and feel.");
        }
        catch (InstantiationException e) {
            System.err.println("Unable to initialize look and feel.");
        }
        catch (IllegalAccessException e) {
            System.err.println("Unable to intiailize look and feel.");
        }
        catch (UnsupportedLookAndFeelException e) {
            System.err.println("Unable to initialize look and feel.");
        }
        String filename = null;
        String component = "SamGUI";
        if (args.length > 2) {
            SamUI.printUsage(component);
        } else if (args.length >= 1) {
            component = "";
            if (args[0].equals("-gui")) {
                component = "SamGUI";
            } else if (args[0].equals("-capture")) {
                component = "SamCapture";
            } else if (args[0].equals("-tester")) {
                component = "SamTester";
            } else if (args.length == 2) {
                SamUI.printUsage("SamGUI");
            }
            if (component == null) {
                component = "SamGUI";
                filename = args[0];
            } else if (args.length == 2) {
                filename = args[1];
            }
        }
        if (component.equals("SamGUI")) {
            SamGUI.startUI(filename);
        } else if (component.equals("SamCapture")) {
            SamCapture.startUI(filename);
        } else if (component.equals("SamTester")) {
            SamTester.startUI(filename);
        } else {
            SamUI.printUsage("SamGUI");
        }
    }

    public static JMenu createSamMenu(final Component parent) {
        JMenu samMenu = new JMenu("SaM");
        samMenu.setMnemonic(83);
        JMenuItem simulatorMenuItem = samMenu.add("Simulator");
        simulatorMenuItem.setMnemonic(83);
        simulatorMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 3));
        simulatorMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.startUI();
            }
        });
        JMenuItem captureMenuItem = samMenu.add("Capture Viewer");
        captureMenuItem.setMnemonic(67);
        captureMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, 3));
        captureMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.startUI();
            }
        });
        JMenuItem testerMenuItem = samMenu.add("Tester");
        testerMenuItem.setMnemonic(84);
        testerMenuItem.setAccelerator(KeyStroke.getKeyStroke(84, 3));
        testerMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.startUI();
            }
        });
        samMenu.addSeparator();
        JMenuItem aboutMenuItem = samMenu.add("About SaM");
        aboutMenuItem.setMnemonic(65);
        aboutMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getAboutDialog().setVisible(true);
            }
        });
        return samMenu;
    }

    public static interface Component {
        public SamAboutDialog getAboutDialog();

        public boolean close();
    }
}

