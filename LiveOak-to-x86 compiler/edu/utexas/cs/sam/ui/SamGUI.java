/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.core.AssemblerException;
import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.SamAssembler;
import edu.utexas.cs.sam.core.SymbolTable;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.Video;
import edu.utexas.cs.sam.core.instructions.Instruction;
import edu.utexas.cs.sam.ui.ProgramCodeCellRenderer;
import edu.utexas.cs.sam.ui.SamCapture;
import edu.utexas.cs.sam.ui.SamUI;
import edu.utexas.cs.sam.ui.components.FileDialogManager;
import edu.utexas.cs.sam.ui.components.GridBagUtils;
import edu.utexas.cs.sam.ui.components.SamAboutDialog;
import edu.utexas.cs.sam.ui.components.SamColorReferenceDialog;
import edu.utexas.cs.sam.ui.components.SamHeapPanel;
import edu.utexas.cs.sam.ui.components.SamRegistersPanel;
import edu.utexas.cs.sam.ui.components.SamStackPanel;
import edu.utexas.cs.sam.ui.components.StatusBar;
import edu.utexas.cs.sam.utils.ClassFileLoader;
import edu.utexas.cs.sam.utils.ProgramState;
import edu.utexas.cs.sam.utils.RegistrationSystem;
import edu.utexas.cs.sam.utils.SamThread;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SamGUI
extends JFrame
implements Video,
SamUI.Component,
SamThread.ThreadParent {
    private static final String BR = System.getProperty("line.separator");
    private File sourceFile = null;
    private String filename;
    private FileDialogManager fileDialogs;
    private static final int CHOOSER_OPEN = 0;
    private static final int CHOOSER_SAVE = 1;
    private static final int CHOOSER_LOADINST = 2;
    private Preferences prefs;
    private Sys sys;
    private Processor proc;
    private Memory mem;
    private RunThread runThread;
    private int runDelay = 64;
    public static int classID = RegistrationSystem.getNextUID();
    private JPanel mainPanel;
    private JPanel programCodePanel;
    private JPanel consolePanel;
    private JPanel buttonPanel;
    private SamStackPanel stackPanel;
    private SamHeapPanel heapPanel;
    private SamRegistersPanel registerPanel;
    private StatusBar statusBar;
    private GridBagLayout componentLayout;
    private GridBagConstraints componentLayoutCons;
    private JMenuItem openMenuItem;
    private JMenuItem saveAsMenuItem;
    private JMenuItem loadInstructionsMenuItem;
    private JMenuItem resetMenuItem;
    private JMenuItem runMenuItem;
    private JMenuItem captureMenuItem;
    private JMenuItem stepMenuItem;
    private JMenuItem stopMenuItem;
    private JMenuItem toggleBreakpointMenuItem;
    private JMenu speedMenu;
    private JButton openButton;
    private JButton resetButton;
    private JButton runButton;
    private JButton captureButton;
    private JButton stepButton;
    private JButton stopButton;
    private JDialog colorsDialog = null;
    private SamAboutDialog aboutDialog = null;
    private JList programCode;
    private JScrollPane programCodeView;
    private boolean breakpointEditingEnabled = false;
    private BreakpointList breakpoints = new BreakpointList();
    private boolean breakpointStop = false;
    private int lastExecuted = -1;
    private boolean capture = false;
    private List<ProgramState> steps;
    private JTextArea simulatorOutput;
    private int curStatus = 0;
    private static final int DEFAULT = 0;
    private static final int RUNCOMPLETED = 1;
    private static final int READYTORUN = 2;
    private static final int RUNNING = 3;
    private static final int CAPTURING = 4;
    private static final int STOPPED = 5;

    protected SamGUI(Sys sys) {
        System.setProperty("sun.awt.noerasebackground", "true");
        RegistrationSystem.register(classID, this);
        this.fileDialogs = new FileDialogManager(3);
        Container contentPane = this.getContentPane();
        this.setTitle("SaM Simulator");
        this.setDefaultCloseOperation(0);
        contentPane.setLayout(new BorderLayout());
        this.mainPanel = new JPanel();
        contentPane.add((Component)this.mainPanel, "Center");
        this.statusBar = new StatusBar();
        contentPane.add((Component)this.statusBar, "South");
        this.sys = sys;
        this.proc = sys.cpu();
        this.mem = sys.mem();
        sys.setVideo(this);
        this.prefs = Preferences.userRoot().node("/edu/cornell/cs/SaM/SamGUI");
        this.createComponents();
        this.createMenus();
        this.reset();
        this.addNotify();
        this.setWindowListeners();
        this.pack();
    }

    private void setWindowListeners() {
        this.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent e) {
            }
        });
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                SamGUI.this.close();
            }
        });
    }

    private void createComponents() {
        this.programCodePanel = this.buildProgramCodePanel();
        this.registerPanel = new SamRegistersPanel();
        this.stackPanel = new SamStackPanel();
        this.consolePanel = this.buildConsolePanel();
        this.buttonPanel = this.buildButtonPanel();
        this.heapPanel = new SamHeapPanel();
        this.componentLayoutCons = new GridBagConstraints();
        this.componentLayoutCons.fill = 1;
        this.componentLayoutCons.anchor = 10;
        this.componentLayoutCons.insets = new Insets(5, 5, 5, 5);
        this.componentLayout = new GridBagLayout();
        this.mainPanel.setLayout(this.componentLayout);
        this.reorderComponents();
    }

    private void reorderComponents() {
        int width = 0;
        this.mainPanel.removeAll();
        int position = 0;
        if (this.prefs.getBoolean("showProgramCodePanel", true)) {
            width += 225;
            GridBagUtils.addComponent(this.programCodePanel, this.mainPanel, this.componentLayout, this.componentLayoutCons, position, 0, 1, 2, 1.0, 1.0);
            ++position;
        }
        if (this.prefs.getBoolean("showStackPanel", true)) {
            width += 175;
            GridBagUtils.addComponent(this.stackPanel, this.mainPanel, this.componentLayout, this.componentLayoutCons, position, 0, 1, 2, 1.0, 1.0);
            ++position;
        }
        if (this.prefs.getBoolean("showHeapPanel", false)) {
            width += 225;
            GridBagUtils.addComponent(this.heapPanel, this.mainPanel, this.componentLayout, this.componentLayoutCons, position, 0, 1, 2, 1.0, 1.0);
            ++position;
        }
        GridBagUtils.addComponent(this.registerPanel, this.mainPanel, this.componentLayout, this.componentLayoutCons, position, 0, 1, 1, 0.0, 0.0);
        Insets oldInsets = this.componentLayoutCons.insets;
        this.componentLayoutCons.insets = new Insets(5, 5, 1, 5);
        GridBagUtils.addComponent(this.buttonPanel, this.mainPanel, this.componentLayout, this.componentLayoutCons, position, 1, 1, 1, 0.0, 1.0);
        this.componentLayoutCons.insets = oldInsets;
        GridBagUtils.addComponent(this.consolePanel, this.mainPanel, this.componentLayout, this.componentLayoutCons, 0, 2, ++position, 1, 1.0, 0.1);
        this.validate();
        this.pack();
        this.setSize(new Dimension((width += 125) < 560 ? 560 : width, 560));
    }

    public JPanel buildProgramCodePanel() {
        this.programCode = new JList(new DefaultListModel());
        this.programCode.setCellRenderer(new ProgramCodeCellRenderer(this.breakpoints));
        this.programCode.setSelectionMode(0);
        this.programCode.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                JList source = (JList)e.getSource();
                SamGUI.this.toggleBreakpointMenuItem.setEnabled(SamGUI.this.breakpointEditingEnabled && source.getSelectedIndex() != -1);
            }
        });
        this.programCode.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                SamGUI.this.toggleBreakpoint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(150, 350));
        p.setMinimumSize(new Dimension(150, 100));
        p.setLayout(new BorderLayout());
        this.programCodeView = new JScrollPane(this.programCode);
        this.programCodeView.setBorder(new SoftBevelBorder(1));
        p.add((Component)this.programCodeView, "Center");
        p.add((Component)new JLabel("Program Code:"), "North");
        return p;
    }

    public JPanel buildConsolePanel() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(525, 100));
        p.setMinimumSize(new Dimension(100, 100));
        p.setLayout(new BorderLayout());
        this.simulatorOutput = new JTextArea();
        JScrollPane simulatorScrollPane = new JScrollPane(this.simulatorOutput);
        simulatorScrollPane.setBorder(new SoftBevelBorder(1));
        p.add((Component)simulatorScrollPane, "Center");
        p.add((Component)new JLabel("Console:"), "North");
        this.simulatorOutput.setEditable(false);
        this.simulatorOutput.setLineWrap(true);
        return p;
    }

    public JPanel buildButtonPanel() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(100, 175));
        p.setLayout(new GridLayout(6, 1, 0, 5));
        this.openButton = new JButton("Open");
        this.openButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                File cdir = SamGUI.this.sourceFile == null ? null : SamGUI.this.sourceFile.getParentFile();
                File selected = SamGUI.this.fileDialogs.getOpenFile(SamGUI.this, "sam", "SaM Program", cdir, 0);
                if (selected != null) {
                    SamGUI.this.reset();
                    SamGUI.this.loadFile(selected);
                }
            }
        });
        p.add(this.openButton);
        this.stepButton = new JButton("Step");
        this.stepButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SamGUI.this.capture) {
                    SamGUI.this.resetCapture();
                }
                SamGUI.this.step();
            }
        });
        p.add(this.stepButton);
        this.runButton = new JButton("Run");
        this.runButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SamGUI.this.capture) {
                    SamGUI.this.resetCapture();
                }
                SamGUI.this.run();
            }
        });
        p.add(this.runButton);
        this.captureButton = new JButton("Capture");
        this.captureButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SamGUI.this.capture) {
                    SamGUI.this.resetCapture();
                }
                SamGUI.this.capture();
            }
        });
        p.add(this.captureButton);
        this.stopButton = new JButton("Stop");
        this.stopButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.stop();
            }
        });
        p.add(this.stopButton);
        this.resetButton = new JButton("Reset");
        this.resetButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.reset();
            }
        });
        p.add(this.resetButton);
        return p;
    }

    private void createMenus() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.setMnemonic(70);
        this.openMenuItem = fileMenu.add("Open...");
        this.openMenuItem.setMnemonic(79);
        this.openMenuItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
        this.openMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                File cdir = SamGUI.this.sourceFile == null ? null : SamGUI.this.sourceFile.getParentFile();
                File selected = SamGUI.this.fileDialogs.getOpenFile(SamGUI.this, "sam", "SaM Program", cdir, 0);
                if (selected != null) {
                    SamGUI.this.reset();
                    SamGUI.this.loadFile(selected);
                }
            }
        });
        this.loadInstructionsMenuItem = fileMenu.add("Load Instruction...");
        this.loadInstructionsMenuItem.setMnemonic(76);
        this.loadInstructionsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                File f = SamGUI.this.fileDialogs.getOpenFile(SamGUI.this, "class", "Instruction Bytecode", null, 2);
                if (f != null) {
                    SamGUI.this.loadInstruction(f);
                }
            }
        });
        this.saveAsMenuItem = fileMenu.add("Save As...");
        this.saveAsMenuItem.setMnemonic(83);
        this.saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        this.saveAsMenuItem.setEnabled(false);
        this.saveAsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                File cdir = SamGUI.this.sourceFile == null ? null : SamGUI.this.sourceFile.getParentFile();
                File savefile = SamGUI.this.fileDialogs.getSaveFile(SamGUI.this, "sam", "SaM Program", cdir, 1);
                if (savefile != null) {
                    SamGUI.this.save(savefile);
                }
            }
        });
        JMenuItem closeMenuItem = fileMenu.add("Close Window");
        closeMenuItem.setMnemonic(67);
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(87, 2));
        closeMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.close();
            }
        });
        JMenuItem exitMenuItem = fileMenu.add("Exit");
        exitMenuItem.setMnemonic(88);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, 2));
        exitMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.close();
                SamUI.exit();
            }
        });
        JMenu runMenu = new JMenu("Run");
        runMenu.setMnemonic(82);
        menuBar.add(runMenu);
        this.runMenuItem = runMenu.add("Run");
        this.runMenuItem.setMnemonic(82);
        this.runMenuItem.setAccelerator(KeyStroke.getKeyStroke(116, 0));
        this.runMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SamGUI.this.capture) {
                    SamGUI.this.resetCapture();
                }
                SamGUI.this.run();
            }
        });
        this.stepMenuItem = runMenu.add("Step");
        this.stepMenuItem.setMnemonic(84);
        this.stepMenuItem.setAccelerator(KeyStroke.getKeyStroke(117, 0));
        this.stepMenuItem.setEnabled(false);
        this.stepMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SamGUI.this.capture) {
                    SamGUI.this.resetCapture();
                }
                SamGUI.this.step();
            }
        });
        this.stopMenuItem = runMenu.add("Stop");
        this.stopMenuItem.setMnemonic(83);
        this.stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(118, 0));
        this.stopMenuItem.setEnabled(false);
        this.stopMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.stop();
            }
        });
        this.resetMenuItem = runMenu.add("Reset");
        this.resetMenuItem.setMnemonic(69);
        this.resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(82, 2));
        this.resetMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.reset();
            }
        });
        this.speedMenu = new JMenu("Execution Speed");
        this.speedMenu.setMnemonic(88);
        runMenu.add(this.speedMenu);
        ButtonGroup speedGroup = new ButtonGroup();
        ExecutionSpeed speed = ExecutionSpeed.fromCode(this.prefs.getInt("executionSpeed", ExecutionSpeed.SPEED_NONE.getCode()));
        for (ExecutionSpeed setting : EnumSet.range(ExecutionSpeed.SPEED_VF, ExecutionSpeed.SPEED_VS)) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(setting.getText());
            this.speedMenu.add(item);
            speedGroup.add(item);
            if (setting.compareTo(speed) == 0) {
                item.setSelected(true);
                this.runDelay = speed.getDelay();
                this.prefs.putInt("executionSpeed", speed.getCode());
                this.prefs.putInt("customExecutionSpeedSetting", 0);
            }
            final ExecutionSpeed sp = setting;
            item.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    SamGUI.this.runDelay = sp.getDelay();
                    SamGUI.this.prefs.putInt("executionSpeed", sp.getCode());
                    SamGUI.this.prefs.putInt("customExecutionSpeedSetting", 0);
                }
            });
        }
        JMenu debugMenu = new JMenu("Debug");
        debugMenu.setMnemonic(68);
        menuBar.add(debugMenu);
        this.captureMenuItem = debugMenu.add("Capture");
        this.captureMenuItem.setMnemonic(67);
        this.captureMenuItem.setAccelerator(KeyStroke.getKeyStroke(119, 0));
        this.captureMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (SamGUI.this.capture) {
                    SamGUI.this.resetCapture();
                }
                SamGUI.this.capture();
            }
        });
        this.toggleBreakpointMenuItem = debugMenu.add("Toggle Breakpoint");
        this.toggleBreakpointMenuItem.setMnemonic(66);
        this.toggleBreakpointMenuItem.setAccelerator(KeyStroke.getKeyStroke(66, 2));
        this.toggleBreakpointMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.toggleBreakpoint();
            }
        });
        JMenuItem deleteBreakpointsMenuItem = debugMenu.add("Remove All Breakpoints");
        deleteBreakpointsMenuItem.setMnemonic(82);
        deleteBreakpointsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.deleteBreakpoints();
            }
        });
        JMenu displayMenu = new JMenu("Display");
        displayMenu.setMnemonic(80);
        menuBar.add(displayMenu);
        JMenuItem colorsMenuItem = displayMenu.add("Stack Colors Reference");
        colorsMenuItem.setMnemonic(67);
        colorsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.this.displayColorReference();
            }
        });
        displayMenu.addSeparator();
        JCheckBoxMenuItem programCodeMenuItem = new JCheckBoxMenuItem("Program Code");
        programCodeMenuItem.setMnemonic(67);
        displayMenu.add(programCodeMenuItem);
        if (this.prefs.getBoolean("showProgramCodePanel", true)) {
            programCodeMenuItem.setState(true);
        }
        programCodeMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JCheckBoxMenuItem)e.getSource()).getState()) {
                    SamGUI.this.prefs.putBoolean("showProgramCodePanel", true);
                } else {
                    SamGUI.this.prefs.putBoolean("showProgramCodePanel", false);
                }
                SamGUI.this.reorderComponents();
                SamGUI.this.updateProgram();
            }
        });
        JCheckBoxMenuItem stackMenuItem = new JCheckBoxMenuItem("Stack");
        stackMenuItem.setMnemonic(83);
        displayMenu.add(stackMenuItem);
        if (this.prefs.getBoolean("showStackPanel", true)) {
            stackMenuItem.setState(true);
        }
        stackMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JCheckBoxMenuItem)e.getSource()).getState()) {
                    SamGUI.this.prefs.putBoolean("showStackPanel", true);
                    SamGUI.this.stackPanel.update(SamGUI.this.mem);
                } else {
                    SamGUI.this.prefs.putBoolean("showStackPanel", false);
                }
                SamGUI.this.reorderComponents();
            }
        });
        JCheckBoxMenuItem heapMenuItem = new JCheckBoxMenuItem("Heap");
        heapMenuItem.setMnemonic(72);
        displayMenu.add(heapMenuItem);
        if (this.prefs.getBoolean("showHeapPanel", false)) {
            heapMenuItem.setState(true);
        }
        heapMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JCheckBoxMenuItem)e.getSource()).getState()) {
                    SamGUI.this.prefs.putBoolean("showHeapPanel", true);
                    SamGUI.this.heapPanel.update(SamGUI.this.mem);
                } else {
                    SamGUI.this.prefs.putBoolean("showHeapPanel", false);
                }
                SamGUI.this.reorderComponents();
            }
        });
        menuBar.add(SamUI.createSamMenu(this));
    }

    @Override
    public boolean close() {
        if (this.colorsDialog != null) {
            this.colorsDialog.dispose();
        }
        if (this.aboutDialog != null) {
            this.aboutDialog.dispose();
        }
        RegistrationSystem.unregister(classID, this);
        this.dispose();
        return true;
    }

    protected void start() {
        this.pack();
        this.setVisible(true);
        this.colorsDialog = new SamColorReferenceDialog(this);
        this.aboutDialog = this.getAboutDialog();
    }

    private void reset() {
        this.proc.init();
        this.mem.init();
        this.programCode.clearSelection();
        if (this.prefs.getBoolean("showStackPanel", true)) {
            this.stackPanel.update(this.mem);
        }
        if (this.prefs.getBoolean("showHeapPanel", true)) {
            this.heapPanel.update(this.mem);
        }
        this.registerPanel.update(this.proc);
        this.updateProgram(true);
        if (this.proc.getProgram() != null) {
            this.setStatus(2);
        } else {
            this.setStatus(0);
        }
        this.updateProgram(false);
        this.simulatorOutput.setText("");
        this.breakpointStop = false;
        this.resetCapture();
    }

    private void resetCapture() {
        this.capture = false;
        this.steps = new ArrayList<ProgramState>();
    }

    private void updateProgram() {
        this.updateProgram(false);
    }

    private void updateProgram(boolean update) {
        if (!this.prefs.getBoolean("showProgramCodePanel", true)) {
            return;
        }
        DefaultListModel prog = (DefaultListModel)this.programCode.getModel();
        if (update) {
            prog.clear();
            Program code = this.proc.getProgram();
            this.lastExecuted = -1;
            if (code != null) {
                SymbolTable ST = code.getSymbolTable();
                int i = 0;
                while (i < code.getLength()) {
                    String label = ST.resolveSymbol(i);
                    prog.addElement(new ProgramCodeCellRenderer.ProgramCodeCell(i, code.getInst(i).toString(), label));
                    ++i;
                }
                ((ProgramCodeCellRenderer.ProgramCodeCell)prog.get(0)).setExecuting(true);
            }
        }
        if (prog.size() > 0) {
            this.setNextExecuting(this.proc.get(0));
        }
        this.programCode.ensureIndexIsVisible(this.proc.get(0));
        this.programCodeView.revalidate();
        this.programCodeView.repaint();
    }

    private void toggleBreakpoint() {
        int ind = this.programCode.getSelectedIndex();
        if (ind == -1) {
            return;
        }
        if (this.breakpoints.checkBreakpoint(ind)) {
            this.breakpoints.deleteBreakpoint(ind);
        } else {
            this.breakpoints.addBreakpoint(ind);
        }
        this.programCodeView.revalidate();
        this.programCodeView.repaint();
    }

    private void deleteBreakpoints() {
        this.breakpoints.deleteAll();
        this.programCodeView.revalidate();
        this.programCodeView.repaint();
    }

    public void loadFile(File samFile) {
        try {
            Program prog = SamAssembler.assemble(new FileReader(samFile));
            this.loadProgram(prog, samFile.getName());
            this.sourceFile = samFile;
        }
        catch (AssemblerException e) {
            this.statusBar.setText("Could not open file");
            this.simulatorOutput.setText("Assembler Error:" + BR + e);
        }
        catch (FileNotFoundException e) {
            this.statusBar.setText("Could not find file");
            this.simulatorOutput.setText("Could not find file");
        }
        catch (IOException e) {
            this.statusBar.setText("Could not load file");
            this.simulatorOutput.setText("I/O Error while processing file");
        }
    }

    public void loadProgram(Program prog, String filename) {
        this.proc.init();
        this.mem.init();
        if (prog == null) {
            this.setStatus(0);
        } else {
            try {
                this.proc.load(prog);
            }
            catch (SystemException e) {
                this.statusBar.setText("Could not load program");
                this.simulatorOutput.setText("Processor Error:" + BR + e);
                return;
            }
            this.breakpoints.deleteAll();
            this.breakpointStop = false;
            this.updateProgram(true);
            this.programCode.clearSelection();
            this.setStatus(2);
        }
        this.sourceFile = null;
        this.filename = filename;
        this.setTitle("SaM Simulator - " + filename);
    }

    public void save(File samFile) {
        if (this.steps == null) {
            return;
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(samFile));
            Program code = this.proc.getProgram();
            if (code != null) {
                SymbolTable ST = code.getSymbolTable();
                int i = 0;
                while (i < code.getLength()) {
                    Collection<String> labels = ST.resolveSymbols(i);
                    if (labels != null) {
                        for (String label : labels) {
                            out.write(String.valueOf(label) + ";" + BR);
                        }
                    }
                    out.write(code.getInst(i) + BR);
                    ++i;
                }
            }
            out.flush();
            out.close();
            this.sourceFile = samFile;
            this.filename = this.sourceFile.getName();
            this.setTitle("SaM Simulator - " + samFile.getName());
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Could not find file", "Error", 0);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file", "Error", 0);
        }
    }

    private void loadInstruction(File f) {
        ClassFileLoader cl = new ClassFileLoader(this.getClass().getClassLoader());
        String className = f.getName();
        if (className.indexOf(46) < 0) {
            JOptionPane.showMessageDialog(this, "Could not load instruction - improper filename.", "Error", 0);
            return;
        }
        if (!className.startsWith("SAM_")) {
            JOptionPane.showMessageDialog(this, "Class name is missing the SAM_ prefix.", "Error", 0);
            return;
        }
        className = className.substring(0, className.indexOf(46));
        String instructionName = className.substring(4);
        try {
            Class<?> c = cl.getClass(f, className);
            Instruction i = (Instruction)c.newInstance();
            SamAssembler.instructions.addInstruction(instructionName, c);
            this.statusBar.setText("Loaded instruction " + instructionName);
        }
        catch (ClassCastException err) {
            JOptionPane.showMessageDialog(this, "Class does not implement the Instruction interface.", "Error", 0);
        }
        catch (NoClassDefFoundError err) {
            JOptionPane.showMessageDialog(this, "Could not load instruction. " + BR + "Check that it is marked public and does not belong to any package.", "Error", 0);
        }
        catch (ClassNotFoundException err) {
            JOptionPane.showMessageDialog(this, "Could not load instruction. " + BR + "Check that it is marked public and does not belong to any package.", "Error", 0);
        }
        catch (InstantiationException err) {
            JOptionPane.showMessageDialog(this, "Could not load instruction. " + BR + "Check that it is marked public and does not belong to any package.", "Error", 0);
        }
        catch (IllegalAccessException err) {
            JOptionPane.showMessageDialog(this, "Could not load instruction. " + BR + "Check that it is marked public and does not belong to any package.", "Error", 0);
        }
    }

    private synchronized void run() {
        this.runThread = new RunThread(this, this.sys, this.runDelay);
        this.runThread.setBreakpointList(this.breakpoints);
        if (this.breakpointStop) {
            this.step();
            this.breakpointStop = false;
        }
        this.setStatus(3);
        this.statusBar.setPermanentText("Running...");
        this.runThread.start();
    }

    private synchronized void capture() {
        this.capture = true;
        this.runThread = new RunThread(this, this.sys, this.runDelay);
        this.runThread.setBreakpointList(this.breakpoints);
        if (this.breakpointStop) {
            this.step();
            this.breakpointStop = false;
        }
        this.setStatus(4);
        this.statusBar.setPermanentText("Capturing...");
        this.runThread.start();
    }

    @Override
    public void threadEvent(final int code, final Object o) {
        try {
            SwingUtilities.invokeAndWait(new Runnable(){

                @Override
                public void run() {
                    SamGUI.this.threadEventReal(code, o);
                }
            });
        }
        catch (InterruptedException interruptedException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
    }

    private synchronized void threadEventReal(int code, Object o) {
        switch (code) {
            case 4: {
                int lastpc = (Integer)o;
                if (this.prefs.getBoolean("showStackPanel", true)) {
                    this.stackPanel.update(this.mem);
                }
                if (this.prefs.getBoolean("showHeapPanel", true)) {
                    this.heapPanel.update(this.mem);
                }
                this.registerPanel.update(this.proc);
                this.updateProgram();
                if (!this.capture) break;
                this.steps.add(new ProgramState(lastpc, this.mem.getStack(), this.proc.getRegisters()));
                break;
            }
            case 2: {
                try {
                    Iterator<HeapAllocator.Allocation> iter;
                    HeapAllocator heap;
                    this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Exit Code: " + this.mem.getMem(0) + BR);
                    if (this.proc.get(1) != 1) {
                        this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Warning: You do not have one item remaining on the stack" + BR);
                    }
                    if ((heap = this.mem.getHeapAllocator()) != null && (iter = heap.getAllocations()).hasNext()) {
                        this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Warning: Your program leaks memory" + BR);
                    }
                }
                catch (SystemException e) {
                    this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "No exit code provided" + BR);
                }
                if (this.capture) {
                    this.statusBar.setText("Capture Completed");
                    SamCapture.startUI(this.steps, this.proc.getProgram(), this.filename);
                } else {
                    this.statusBar.setText("Run Completed");
                }
                this.setStatus(1);
                this.setNextExecuting(-1);
                break;
            }
            case 1: {
                this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Processor Error: " + o.toString() + BR);
                this.statusBar.setText("Processor Error");
                this.setStatus(1);
                break;
            }
            case 0: {
                this.setStatus(5);
                if (this.capture) {
                    this.statusBar.setText("Capture Interrupted");
                    SamCapture.startUI(this.steps, this.proc.getProgram(), this.filename);
                    break;
                }
                this.statusBar.setText("Execution Stopped");
                break;
            }
            case 3: {
                this.setStatus(5);
                this.statusBar.setText("Breakpoint Reached");
                if (this.capture) {
                    this.statusBar.setText("Capture Completed");
                    SamCapture.startUI(this.steps, this.proc.getProgram(), this.filename);
                }
                this.breakpointStop = true;
                break;
            }
            default: {
                this.statusBar.clearText();
                this.setStatus(1);
            }
        }
        this.programCodeView.revalidate();
        this.programCodeView.repaint();
        this.validate();
    }

    private void setNextExecuting(int pc) {
        if (this.lastExecuted > -1) {
            ((ProgramCodeCellRenderer.ProgramCodeCell)((DefaultListModel)this.programCode.getModel()).get(this.lastExecuted)).setExecuting(false);
        }
        this.lastExecuted = pc;
        if (pc > -1) {
            ((ProgramCodeCellRenderer.ProgramCodeCell)((DefaultListModel)this.programCode.getModel()).get(pc)).setExecuting(true);
        }
    }

    private synchronized void stop() {
        if (this.runThread != null) {
            this.runThread.interrupt();
        }
    }

    private void step() {
        this.setStatus(5);
        int lastpc = this.proc.get(0);
        try {
            this.proc.step();
            if (this.proc.get(3) != 0) {
                Iterator<HeapAllocator.Allocation> iter;
                HeapAllocator heap;
                this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Exit Code: " + this.mem.getMem(0) + BR);
                if (this.proc.get(1) != 1) {
                    this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Warning: You do not have one item remaining on the stack" + BR);
                }
                if ((heap = this.mem.getHeapAllocator()) != null && (iter = heap.getAllocations()).hasNext()) {
                    this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Warning: Your program leaks memory" + BR);
                }
                this.setStatus(1);
                this.statusBar.setText("Execution completed");
                this.threadEventReal(4, lastpc);
                this.setNextExecuting(-1);
                this.programCodeView.revalidate();
                this.programCodeView.repaint();
                this.validate();
                return;
            }
        }
        catch (SystemException e) {
            this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + e + BR);
            this.setStatus(1);
            this.setNextExecuting(-1);
            this.programCodeView.revalidate();
            this.programCodeView.repaint();
            this.validate();
            return;
        }
        this.threadEventReal(4, lastpc);
    }

    private void setStatus(int status) {
        switch (status) {
            case 0: {
                this.enableButtons(false, false, false, false, true, false, true, false);
                break;
            }
            case 1: {
                this.enableButtons(false, true, false, false, true, true, true, true);
                break;
            }
            case 2: {
                this.enableButtons(true, true, true, false, true, false, true, true);
                break;
            }
            case 3: {
                this.enableButtons(false, false, false, true, false, false, false, false);
                break;
            }
            case 4: {
                this.enableButtons(false, false, false, true, false, false, false, false);
                break;
            }
            case 5: {
                this.enableButtons(true, true, true, false, true, true, true, true);
            }
        }
        this.curStatus = status;
    }

    private void enableButtons(boolean runStep, boolean breakpoint, boolean capture, boolean stop, boolean open, boolean reset, boolean runOptions, boolean save) {
        this.resetMenuItem.setEnabled(reset);
        this.resetButton.setEnabled(reset);
        this.stopMenuItem.setEnabled(stop);
        this.stopButton.setEnabled(stop);
        this.captureMenuItem.setEnabled(capture);
        this.captureButton.setEnabled(capture);
        this.runMenuItem.setEnabled(runStep);
        this.runButton.setEnabled(runStep);
        this.speedMenu.setEnabled(runOptions);
        this.stepMenuItem.setEnabled(runStep);
        this.stepButton.setEnabled(runStep);
        this.openMenuItem.setEnabled(open);
        this.openButton.setEnabled(open);
        this.saveAsMenuItem.setEnabled(save);
        this.breakpointEditingEnabled = breakpoint;
        if (runStep) {
            this.runButton.setBackground(new Color(204, 255, 204));
            this.stepButton.setBackground(new Color(255, 255, 204));
        } else {
            this.runButton.setBackground(new Color(204, 220, 204));
            this.stepButton.setBackground(new Color(220, 220, 204));
        }
        if (capture) {
            this.captureButton.setBackground(new Color(220, 204, 255));
        } else {
            this.captureButton.setBackground(new Color(212, 204, 220));
        }
        if (stop) {
            this.stopButton.setBackground(new Color(255, 204, 204));
        } else {
            this.stopButton.setBackground(new Color(220, 204, 204));
        }
        if (open) {
            this.openButton.setBackground(new Color(204, 204, 255));
        } else {
            this.openButton.setBackground(new Color(204, 204, 220));
        }
        if (reset) {
            this.resetButton.setBackground(new Color(255, 255, 255));
        } else {
            this.resetButton.setBackground(new Color(220, 220, 220));
        }
    }

    private void displayColorReference() {
        if (this.colorsDialog == null) {
            this.colorsDialog = new SamColorReferenceDialog(this);
        }
        this.colorsDialog.setVisible(true);
    }

    private void displayAbout() {
    }

    @Override
    public void writeString(String str) {
        this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Processor Output: " + str + BR);
    }

    @Override
    public void writeInt(int i) {
        this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Processor Output: " + i + BR);
    }

    @Override
    public void writeChar(char c) {
        this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Processor Output: " + c + BR);
    }

    @Override
    public void writeFloat(float f) {
        this.simulatorOutput.setText(String.valueOf(this.simulatorOutput.getText()) + "Processor Output: " + f + BR);
    }

    @Override
    public String readString() {
        String str = JOptionPane.showInputDialog(this, "Enter a String:", "Prompt", 3);
        return str == null ? "" : str;
    }

    @Override
    public int readInt() {
        while (true) {
            try {
                String ans = JOptionPane.showInputDialog(this, "Enter an Integer:", "Prompt", 3);
                int i = ans == null ? 0 : Integer.parseInt(ans);
                return i;
            }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter an integer", "Error", 0);
                continue;
            }
            break;
        }
    }

    @Override
    public float readFloat() {
        while (true) {
            try {
                String ans = JOptionPane.showInputDialog(this, "Enter a Float:", "Prompt", 3);
                float f = ans == null ? 0.0f : Float.parseFloat(ans);
                return f;
            }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a float", "Error", 0);
                continue;
            }
            break;
        }
    }

    @Override
    public char readChar() {
        String ans;
        while ((ans = JOptionPane.showInputDialog(this, "Enter a Character:", "Prompt", 3)) != null) {
            if (ans.length() == 1) {
                return ans.charAt(0);
            }
            JOptionPane.showMessageDialog(this, "Please enter an Character", "Error", 0);
        }
        return '\u0000';
    }

    @Override
    public SamAboutDialog getAboutDialog() {
        if (this.aboutDialog == null) {
            this.aboutDialog = new SamAboutDialog("SaM", "2.6.2", "SaM Simulator", this);
        }
        return this.aboutDialog;
    }

    public static void startUI() {
        SamGUI.startUI(null);
    }

    public static void startUI(String filename) {
        SamGUI gui = new SamGUI(new Sys());
        gui.start();
        if (filename != null) {
            gui.loadFile(new File(filename));
        }
    }

    public static void startUI(Program prog, String filename, Sys sys) {
        SamGUI gui = sys != null ? new SamGUI(sys) : new SamGUI(new Sys());
        gui.start();
        if (prog != null && filename != null) {
            gui.loadProgram(prog, filename);
        }
    }

    public static class BreakpointList {
        protected HashMap<Integer, Boolean> breakpoints = new HashMap();

        public void addBreakpoint(int pc) {
            if (!this.checkBreakpoint(pc)) {
                this.breakpoints.put(pc, true);
            }
        }

        public void addBreakpoints(BreakpointList l) {
            this.breakpoints.putAll(l.breakpoints);
        }

        public boolean checkBreakpoint(int pc) {
            return this.breakpoints.containsKey(pc);
        }

        public void deleteBreakpoint(int pc) {
            this.breakpoints.remove(pc);
        }

        public void deleteBreakpoints(BreakpointList l) {
            for (Integer e : l.breakpoints.keySet()) {
                this.breakpoints.remove(e);
            }
        }

        public void deleteAll() {
            this.breakpoints.clear();
        }
    }

    private static enum ExecutionSpeed {
        SPEED_NONE(0, "Very Fast Execution"),
        SPEED_VF(0, "Very Fast Execution"),
        SPEED_F(25, "Fast Execution"),
        SPEED_M(100, "Medium Execution"),
        SPEED_S(400, "Slow Execution"),
        SPEED_VS(1600, "Very Slow Execution");

        private int delay;
        private String text;

        private ExecutionSpeed(int delay, String text) {
            this.delay = delay;
            this.text = text;
        }

        public int getCode() {
            return this.ordinal();
        }

        public String getText() {
            return this.text;
        }

        public int getDelay() {
            return this.delay;
        }

        public static ExecutionSpeed fromCode(int code) {
            switch (code) {
                case 0: {
                    return SPEED_NONE;
                }
                case 1: {
                    return SPEED_VF;
                }
                case 2: {
                    return SPEED_F;
                }
                case 3: {
                    return SPEED_M;
                }
                case 4: {
                    return SPEED_S;
                }
                case 5: {
                    return SPEED_VS;
                }
            }
            return SPEED_NONE;
        }
    }

    public class RunThread
    extends SamThread {
        protected Processor proc;
        protected Sys sys;
        protected int delay = 50;
        protected BreakpointList breakpoints = null;
        public static final int THREAD_BREAKPOINT = 3;
        public static final int THREAD_STEP = 4;

        public RunThread(SamThread.ThreadParent parent, Sys sys, int delay) {
            this.setParent(parent);
            this.sys = sys;
            this.delay = delay;
            this.proc = sys.cpu();
        }

        public void setBreakpointList(BreakpointList l) {
            this.breakpoints = l;
        }

        public BreakpointList getBreakpointList() {
            return this.breakpoints;
        }

        @Override
        public void execute() throws Exception {
            SamThread.ThreadParent parent = this.getParent();
            while (this.proc.get(3) == 0) {
                if (this.interruptRequested()) {
                    parent.threadEvent(0, null);
                    return;
                }
                int executing = this.proc.get(0);
                if (this.breakpoints != null && this.breakpoints.checkBreakpoint(executing)) {
                    parent.threadEvent(3, null);
                    return;
                }
                this.proc.step();
                parent.threadEvent(4, executing);
                try {
                    if (this.delay <= 0) continue;
                    Thread.sleep(this.delay);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            parent.threadEvent(2, null);
        }
    }
}

