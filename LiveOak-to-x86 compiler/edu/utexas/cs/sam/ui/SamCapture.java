/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.ui.SamGUI;
import edu.utexas.cs.sam.ui.SamUI;
import edu.utexas.cs.sam.ui.StepDisplay;
import edu.utexas.cs.sam.ui.components.FileDialogManager;
import edu.utexas.cs.sam.ui.components.GridBagUtils;
import edu.utexas.cs.sam.ui.components.SamAboutDialog;
import edu.utexas.cs.sam.ui.components.SamColorReferenceDialog;
import edu.utexas.cs.sam.ui.components.StatusBar;
import edu.utexas.cs.sam.utils.ProgramState;
import edu.utexas.cs.sam.utils.RegistrationSystem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SamCapture
extends JFrame
implements SamUI.Component {
    protected static final int CHOOSER_OPEN = 0;
    protected static final int CHOOSER_SAVE = 1;
    protected Container contentPane;
    protected JPanel mainPanel;
    private GridBagConstraints c = new GridBagConstraints();
    private GridBagLayout l = new GridBagLayout();
    protected JList instructionList;
    protected JScrollPane instructionListView;
    protected JMenuItem increaseDisplayMenuItem;
    protected JMenuItem removeDisplayMenuItem;
    protected StepDisplay[] stepDisplays;
    protected List<? extends ProgramState> steps;
    protected JPanel instructionListPanel;
    protected File sourceFile = null;
    protected String filename = null;
    protected Program program;
    protected StatusBar statusBar;
    protected JMenuItem saveAsMenuItem;
    protected JMenuItem openSimMenuItem;
    protected JDialog colorsDialog;
    protected SamAboutDialog aboutDialog;
    protected FileDialogManager fileDialogs;
    public static int classID = RegistrationSystem.getNextUID();

    protected SamCapture() {
        RegistrationSystem.register(classID, this);
        this.fileDialogs = new FileDialogManager(2);
        this.contentPane = this.getContentPane();
        this.setTitle("Capture Viewer");
        this.setDefaultCloseOperation(0);
        this.contentPane.setLayout(new BorderLayout());
        this.mainPanel = new JPanel();
        this.contentPane.add((Component)this.mainPanel, "Center");
        this.statusBar = new StatusBar();
        this.contentPane.add((Component)this.statusBar, "South");
        int sdCount = ((int)this.getToolkit().getScreenSize().getWidth() - 205 - 10) / 155;
        this.stepDisplays = new StepDisplay[sdCount];
        this.createComponents();
        this.createMenus();
        this.addNotify();
        this.setWindowListeners();
        this.resize();
        this.pack();
    }

    /*
     * Unable to fully structure code
     */
    protected void start(List<? extends ProgramState> steps, Program prog, String filename) {
        this.setTitle("Capture Viewer - " + filename);
        this.filename = filename;
        this.steps = steps;
        if (steps.size() != 0) ** GOTO lbl9
        this.close();
        return;
lbl-1000:
        // 1 sources

        {
            this.removeStepDisplay();
lbl9:
            // 2 sources

            ** while (this.stepDisplays.length > steps.size())
        }
lbl10:
        // 1 sources

        this.program = prog;
        instructions = (DefaultListModel)this.instructionList.getModel();
        if (prog != null) {
            i = 0;
            while (i < steps.size()) {
                ins = this.program.getInst(steps.get(i).getLastPC());
                ins.setProgram(prog);
                instructions.addElement(ins);
                ++i;
            }
            this.loadInstruction(0);
            this.instructionList.setSelectedIndex(0);
            this.saveAsMenuItem.setEnabled(true);
            this.openSimMenuItem.setEnabled(true);
        }
        this.start();
    }

    protected void start() {
        this.pack();
        this.setVisible(true);
        this.aboutDialog = this.getAboutDialog();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(this.stepDisplays.length * 150 + 200, 560);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.stepDisplays.length * 150 + 200, 560);
    }

    private void resize() {
        this.setSize(this.stepDisplays.length * 160 + 210, 560);
    }

    public void loadFile(File secFile) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(secFile));
            String version = (String)ois.readObject();
            Program inProg = (Program)ois.readObject();
            List inSteps = (List)ois.readObject();
            ois.close();
            this.sourceFile = secFile;
            this.filename = this.sourceFile.getName();
            this.start(inSteps, inProg, secFile.getName());
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Could not find file", "Error", 0);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error opening file", "Error", 0);
        }
        catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Invalid file", "Error", 0);
        }
        catch (ClassCastException e) {
            JOptionPane.showMessageDialog(this, "Invalid file", "Error", 0);
        }
    }

    public void saveCapture() {
        File cdir = this.sourceFile == null ? null : this.sourceFile.getParentFile();
        File secFile = this.fileDialogs.getSaveFile(this, "sec", "SaM Execution Capture", cdir, 1);
        if (secFile == null) {
            return;
        }
        if (this.steps == null) {
            return;
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(secFile));
            oos.writeObject("2.6.2");
            oos.writeObject(this.program);
            oos.writeObject(this.steps);
            oos.close();
            this.sourceFile = secFile;
            this.filename = this.sourceFile.getName();
            this.setTitle("Capture Viewer - " + secFile.getName());
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Could not find file", "Error", 0);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file", "Error", 0);
        }
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
                SamCapture.this.close();
            }
        });
    }

    private void createMenus() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.setMnemonic(70);
        JMenuItem openMenuItem = fileMenu.add("Open...");
        openMenuItem.setMnemonic(79);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
        openMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                File cdir = SamCapture.this.sourceFile == null ? null : SamCapture.this.sourceFile.getParentFile();
                File selected = SamCapture.this.fileDialogs.getOpenFile(SamCapture.this, "sec", "SaM Execution Capture", cdir, 0);
                if (selected != null) {
                    SamCapture.this.loadFile(selected);
                }
            }
        });
        this.openSimMenuItem = fileMenu.add("Open Program in Simulator");
        this.openSimMenuItem.setMnemonic(77);
        this.openSimMenuItem.setAccelerator(KeyStroke.getKeyStroke(77, 2));
        this.openSimMenuItem.setEnabled(false);
        this.openSimMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.startUI(SamCapture.this.program, new String(SamCapture.this.filename), null);
            }
        });
        this.saveAsMenuItem = fileMenu.add("Save As...");
        this.saveAsMenuItem.setMnemonic(83);
        this.saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        this.saveAsMenuItem.setEnabled(false);
        this.saveAsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.this.saveCapture();
            }
        });
        JMenuItem closeMenuItem = fileMenu.add("Close Window");
        closeMenuItem.setMnemonic(67);
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(87, 2));
        closeMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.this.close();
            }
        });
        JMenuItem exitMenuItem = fileMenu.add("Exit");
        exitMenuItem.setMnemonic(88);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, 2));
        exitMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.this.close();
                SamUI.exit();
            }
        });
        JMenu displayMenu = new JMenu("Display");
        displayMenu.setMnemonic(68);
        menuBar.add(displayMenu);
        this.increaseDisplayMenuItem = displayMenu.add("Add Instruction Display");
        this.increaseDisplayMenuItem.setMnemonic(73);
        this.increaseDisplayMenuItem.setAccelerator(KeyStroke.getKeyStroke(61, 2));
        this.increaseDisplayMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.this.addStepDisplay();
            }
        });
        this.removeDisplayMenuItem = displayMenu.add("Remove Instruction Display");
        this.removeDisplayMenuItem.setMnemonic(73);
        if (this.stepDisplays.length <= 1) {
            this.removeDisplayMenuItem.setEnabled(false);
        }
        this.removeDisplayMenuItem.setAccelerator(KeyStroke.getKeyStroke(45, 2));
        this.removeDisplayMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.this.removeStepDisplay();
            }
        });
        displayMenu.addSeparator();
        JMenuItem colorsMenuItem = displayMenu.add("Stack Colors Reference");
        colorsMenuItem.setMnemonic(83);
        colorsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamCapture.this.displayColorReference();
            }
        });
        menuBar.add(SamUI.createSamMenu(this));
    }

    private void createComponents() {
        this.c.fill = 1;
        this.c.anchor = 10;
        this.c.insets = new Insets(5, 5, 5, 5);
        this.mainPanel.setLayout(this.l);
        this.instructionListPanel = this.buildInstructionListPanel();
        GridBagUtils.addComponent(this.instructionListPanel, this.mainPanel, this.l, this.c, 0, 0, 1, 1, 1.0, 1.0);
        int i = 0;
        while (i < this.stepDisplays.length) {
            this.stepDisplays[i] = new StepDisplay();
            GridBagUtils.addComponent(this.stepDisplays[i], this.mainPanel, this.l, this.c, 1 + i, 0, 1, 1, 1.0, 1.0);
            ++i;
        }
    }

    private JPanel buildInstructionListPanel() {
        this.instructionList = new JList(new DefaultListModel());
        this.instructionList.setSelectionMode(0);
        this.instructionList.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                SamCapture.this.loadInstruction(((JList)e.getSource()).getSelectedIndex());
            }
        });
        JPanel instructionListPanel = new JPanel();
        instructionListPanel.setPreferredSize(new Dimension(200, 350));
        instructionListPanel.setMinimumSize(new Dimension(200, 350));
        instructionListPanel.setLayout(new BorderLayout());
        this.instructionListView = new JScrollPane(this.instructionList);
        this.instructionListView.setBorder(new SoftBevelBorder(1));
        instructionListPanel.add((Component)this.instructionListView, "Center");
        instructionListPanel.add((Component)new JLabel("Instructions Executed:"), "North");
        return instructionListPanel;
    }

    void loadInstruction(int index) {
        if (index < 0) {
            return;
        }
        int currentPosition = this.stepDisplays.length % 2 == 1 ? (this.stepDisplays.length - 1) / 2 : this.stepDisplays.length / 2 - 1;
        while (index - currentPosition < 0) {
            --currentPosition;
        }
        while (index + (this.stepDisplays.length - 1 - currentPosition) >= this.steps.size()) {
            ++currentPosition;
        }
        int startIndex = index - currentPosition;
        int endIndex = index + (this.stepDisplays.length - currentPosition - 1);
        if (endIndex > this.steps.size() - 1) {
            endIndex = this.steps.size() - 1;
        }
        int i = 0;
        while (startIndex + i <= endIndex) {
            this.stepDisplays[i].load(this.steps.get(startIndex + i), this.program);
            if (i == currentPosition) {
                this.stepDisplays[i].setCurrent(true);
            } else {
                this.stepDisplays[i].setCurrent(false);
            }
            ++i;
        }
    }

    void addStepDisplay() {
        if (this.steps != null && this.stepDisplays.length == this.steps.size()) {
            this.increaseDisplayMenuItem.setEnabled(false);
            return;
        }
        StepDisplay[] nsd = new StepDisplay[this.stepDisplays.length + 1];
        System.arraycopy(this.stepDisplays, 0, nsd, 0, this.stepDisplays.length);
        this.stepDisplays = nsd;
        this.stepDisplays[this.stepDisplays.length - 1] = new StepDisplay();
        this.resize();
        GridBagUtils.addComponent(this.stepDisplays[this.stepDisplays.length - 1], this.mainPanel, this.l, this.c, this.stepDisplays.length, 0, 1, 1, 1.0, 1.0);
        this.validate();
        this.loadInstruction(this.instructionList.getSelectedIndex());
        if (this.stepDisplays.length == 2) {
            this.removeDisplayMenuItem.setEnabled(true);
        }
        if (this.steps != null && this.stepDisplays.length == this.steps.size()) {
            this.increaseDisplayMenuItem.setEnabled(false);
        }
    }

    void removeStepDisplay() {
        if (this.stepDisplays.length == 1) {
            return;
        }
        StepDisplay[] nsd = new StepDisplay[this.stepDisplays.length - 1];
        System.arraycopy(this.stepDisplays, 0, nsd, 0, this.stepDisplays.length - 1);
        this.mainPanel.remove(this.stepDisplays[this.stepDisplays.length - 1]);
        this.stepDisplays = nsd;
        this.resize();
        this.pack();
        this.loadInstruction(this.instructionList.getSelectedIndex());
        if (this.stepDisplays.length == 1) {
            this.removeDisplayMenuItem.setEnabled(false);
        }
    }

    private void displayColorReference() {
        if (this.colorsDialog == null) {
            this.colorsDialog = new SamColorReferenceDialog(this);
        }
        this.colorsDialog.setVisible(true);
    }

    @Override
    public SamAboutDialog getAboutDialog() {
        if (this.aboutDialog == null) {
            this.aboutDialog = new SamAboutDialog("SaM", "2.6.2", "SaM Tester", this);
        }
        return this.aboutDialog;
    }

    public static void startUI() {
        SamCapture.startUI(null);
    }

    @Override
    public boolean close() {
        RegistrationSystem.unregister(classID, this);
        this.dispose();
        return true;
    }

    public static void startUI(String filename) {
        SamCapture gui = new SamCapture();
        gui.start();
        if (filename != null) {
            gui.loadFile(new File(filename));
        }
    }

    public static void startUI(List<? extends ProgramState> steps, Program prog, String filename) {
        SamCapture gui = new SamCapture();
        gui.start(steps, prog, filename);
    }
}

