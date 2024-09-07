/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.ui.SamGUI;
import edu.utexas.cs.sam.ui.SamUI;
import edu.utexas.cs.sam.ui.TestScript;
import edu.utexas.cs.sam.ui.TestTableCellRenderer;
import edu.utexas.cs.sam.ui.components.FileDialogManager;
import edu.utexas.cs.sam.ui.components.GridBagUtils;
import edu.utexas.cs.sam.ui.components.SamAboutDialog;
import edu.utexas.cs.sam.ui.components.StatusBar;
import edu.utexas.cs.sam.utils.RegistrationSystem;
import edu.utexas.cs.sam.utils.SamThread;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class SamTester
extends JFrame
implements SamUI.Component,
SamThread.ThreadParent {
    protected static final int STATUS_NOTREADY = 1;
    protected static final int STATUS_EMPTY = 2;
    protected static final int STATUS_LOADED = 3;
    protected static final int STATUS_RUNNING = 4;
    protected static final int CHOOSER_OPEN = 0;
    protected static final int CHOOSER_SAVE = 1;
    protected static final int CHOOSER_SAMPROGRAM = 2;
    protected static String scriptFileExtension = "sts";
    protected static String scriptFileTypeName = "SaM Test Script";
    protected static String testFileExtension = "sam";
    protected static String testFileTypeName = "SaM Program";
    protected Container contentPane;
    protected JPanel mainPanel;
    protected JPanel buttonPanel;
    protected StatusBar statusBar;
    protected JTable tests;
    protected DefaultTableModel testData;
    protected TestTableCellRenderer testRenderer;
    protected TestScript testScript;
    protected JScrollPane testsView;
    protected JMenuItem newFileMenuItem;
    protected JMenuItem openFileMenuItem;
    protected JMenuItem saveFileMenuItem;
    protected JMenuItem saveAsFileMenuItem;
    protected JMenuItem runRunMenuItem;
    protected JMenuItem stopRunMenuItem;
    protected JMenuItem addTestsMenuItem;
    protected JMenuItem deleteTestsMenuItem;
    protected boolean deleteTestsEnabled;
    protected JButton openButton;
    protected JButton runButton;
    protected JButton stopButton;
    protected boolean modified = false;
    protected SamAboutDialog aboutDialog;
    protected TestScript.TestThread testThread;
    protected FileDialogManager fileDialogs;
    public static int classID = RegistrationSystem.getNextUID();

    protected SamTester() {
        System.setProperty("sun.awt.noerasebackground", "true");
        RegistrationSystem.register(classID, this);
        this.fileDialogs = new FileDialogManager(3);
        this.contentPane = this.getContentPane();
        this.setTitle("SaM Tester");
        this.setDefaultCloseOperation(0);
        this.contentPane.setLayout(new BorderLayout());
        this.mainPanel = new JPanel();
        this.contentPane.add((Component)this.mainPanel, "Center");
        this.statusBar = new StatusBar();
        this.contentPane.add((Component)this.statusBar, "South");
        this.createComponents();
        this.setJMenuBar(this.createMenus());
        this.setStatus(1);
        this.addNotify();
        this.setWindowListeners();
        this.pack();
    }

    protected void start() {
        this.setVisible(true);
        this.aboutDialog = this.getAboutDialog();
    }

    protected void updateTitle() {
        if (this.testScript == null || this.testScript.getSourceFile() == null) {
            this.setTitle("SaM Tester");
        } else {
            this.setTitle("SaM Tester - " + this.testScript.getSourceFile().getName());
        }
    }

    protected void createComponents() {
        this.buttonPanel = this.createButtonPanel();
        this.createTable();
        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.insets = new Insets(5, 5, 5, 5);
        this.mainPanel.setLayout(l);
        this.testsView = new JScrollPane(this.tests);
        this.testsView.getViewport().setBackground(Color.WHITE);
        GridBagUtils.addComponent(new JLabel("Tests:"), this.mainPanel, l, c, 0, 0, 2, 1, 1.0, 1.0);
        GridBagUtils.addComponent(this.testsView, this.mainPanel, l, c, 0, 1, 1, 1, 1.0, 1.0);
        GridBagUtils.addComponent(this.buttonPanel, this.mainPanel, l, c, 1, 1, 1, 1, 1.0, 1.0);
    }

    protected JPanel createButtonPanel() {
        JPanel p = new JPanel();
        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.insets = new Insets(5, 5, 5, 5);
        p.setLayout(l);
        this.openButton = GridBagUtils.addButton("Open", p, l, c, 0, 0, 1, 1, 1.0, 1.0);
        this.openButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.openScript();
            }
        });
        this.runButton = GridBagUtils.addButton("Run Tests", p, l, c, 0, 1, 1, 1, 1.0, 1.0);
        this.runButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.runTests();
            }
        });
        this.stopButton = GridBagUtils.addButton("Stop Run", p, l, c, 0, 2, 1, 1, 1.0, 1.0);
        this.stopButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.stopTests();
            }
        });
        return p;
    }

    protected void createTable() {
        Object[] columns = new String[]{"Name", "Status", "Expected", "Actual", "I/O"};
        this.testData = new DefaultTableModel(0, 5){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.testData.setColumnIdentifiers(columns);
        this.tests = new JTable(this.testData);
        this.tests.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel source = (ListSelectionModel)e.getSource();
                SamTester.this.deleteTestsMenuItem.setEnabled(SamTester.this.deleteTestsEnabled && !source.isSelectionEmpty());
            }
        });
        this.tests.setShowVerticalLines(false);
        this.tests.setShowHorizontalLines(false);
        this.tests.setPreferredScrollableViewportSize(new Dimension(500, 100));
        this.tests.setBackground(Color.WHITE);
        Enumeration<TableColumn> e = this.tests.getColumnModel().getColumns();
        this.testRenderer = new TestTableCellRenderer();
        TableColumn first = e.nextElement();
        first.setPreferredWidth(first.getPreferredWidth() * 2);
        while (e.hasMoreElements()) {
            e.nextElement().setCellRenderer(this.testRenderer);
        }
        this.tests.doLayout();
        this.tests.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                SamTester.this.openTestDetails(SamTester.this.tests.rowAtPoint(e.getPoint()));
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
    }

    protected JMenuBar createMenus() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(this.createFileMenu());
        menuBar.add(this.createRunMenu());
        menuBar.add(this.createTestsMenu());
        menuBar.add(SamUI.createSamMenu(this));
        return menuBar;
    }

    protected JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(70);
        this.newFileMenuItem = fileMenu.add("New");
        this.newFileMenuItem.setMnemonic(78);
        this.newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
        this.newFileMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.newScript();
            }
        });
        this.openFileMenuItem = fileMenu.add("Open");
        this.openFileMenuItem.setMnemonic(79);
        this.openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
        this.openFileMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.openScript();
            }
        });
        this.saveFileMenuItem = fileMenu.add("Save");
        this.saveFileMenuItem.setMnemonic(83);
        this.saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        this.saveFileMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.saveScript(false);
            }
        });
        this.saveAsFileMenuItem = fileMenu.add("Save As...");
        this.saveAsFileMenuItem.setMnemonic(65);
        this.saveAsFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(123, 0));
        this.saveAsFileMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.saveScript(true);
            }
        });
        JMenuItem closeFileMenuItem = fileMenu.add("Close Window");
        closeFileMenuItem.setMnemonic(67);
        closeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(87, 2));
        closeFileMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.close();
            }
        });
        JMenuItem exitFileMenuItem = fileMenu.add("Exit");
        exitFileMenuItem.setMnemonic(88);
        exitFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, 2));
        exitFileMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.close();
                SamUI.exit();
            }
        });
        return fileMenu;
    }

    protected JMenu createRunMenu() {
        JMenu runMenu = new JMenu("Run");
        runMenu.setMnemonic(82);
        this.runRunMenuItem = runMenu.add("Run Tests");
        this.runRunMenuItem.setMnemonic(82);
        this.runRunMenuItem.setAccelerator(KeyStroke.getKeyStroke(82, 2));
        this.runRunMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.runTests();
            }
        });
        this.stopRunMenuItem = runMenu.add("Stop Tests");
        this.stopRunMenuItem.setMnemonic(83);
        this.stopRunMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        this.stopRunMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.stopTests();
            }
        });
        return runMenu;
    }

    protected JMenu createTestsMenu() {
        JMenu testsMenu = new JMenu("Tests");
        testsMenu.setMnemonic(84);
        this.addTestsMenuItem = testsMenu.add("Add Test");
        this.addTestsMenuItem.setMnemonic(65);
        this.addTestsMenuItem.setAccelerator(KeyStroke.getKeyStroke(155, 0));
        this.addTestsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.addTest();
            }
        });
        this.deleteTestsMenuItem = testsMenu.add("Delete Test");
        this.deleteTestsMenuItem.setMnemonic(68);
        this.deleteTestsMenuItem.setAccelerator(KeyStroke.getKeyStroke(127, 0));
        this.deleteTestsMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamTester.this.deleteTest();
            }
        });
        return testsMenu;
    }

    protected void newScript() {
        if (!this.confirmClose()) {
            return;
        }
        this.clear();
        this.testScript = this.getNewTestScript();
        this.testRenderer.setTestScript(this.testScript);
        this.setStatus(2);
    }

    protected TestScript getNewTestScript() {
        return new TestScript();
    }

    protected void clear() {
        this.testScript = null;
        this.modified = false;
        this.testData.setRowCount(0);
    }

    protected void openScript() {
        if (!this.confirmClose()) {
            return;
        }
        File file = this.fileDialogs.getOpenFile(this, scriptFileExtension, scriptFileTypeName, null, 0);
        if (file != null) {
            this.clear();
            this.loadFile(file);
        }
    }

    protected void loadFile(File in) {
        try {
            this.testScript = this.getNewTestScript();
            this.testScript.load(new BufferedInputStream(new FileInputStream(in)));
            this.testScript.setSourceFile(in);
            this.testRenderer.setTestScript(this.testScript);
            this.updateTable();
            if (this.testScript.getTests().size() > 0) {
                this.setStatus(3);
            }
            this.updateTitle();
        }
        catch (FileNotFoundException e) {
            this.error("Requested File (" + in.getName() + ") Not Found");
        }
        catch (TestScript.TestScriptException e) {
            this.error("Error parsing test script:\n" + e.getMessage());
        }
    }

    protected boolean saveScript(boolean saveAs) {
        File cdir;
        File toSave = this.testScript.getSourceFile();
        if ((saveAs || toSave == null) && (toSave = this.fileDialogs.getSaveFile(this, scriptFileExtension, scriptFileTypeName, cdir = toSave == null ? null : toSave.getParentFile(), 1)) == null) {
            return false;
        }
        try {
            this.testScript.save(toSave);
            this.testScript.setSourceFile(toSave);
            this.updateTitle();
            this.modified = false;
            return true;
        }
        catch (TestScript.TestScriptException e) {
            this.error("Error saving file:\n" + e.getMessage());
            return false;
        }
    }

    protected void updateTable() {
        this.testData.setRowCount(0);
        if (this.testScript == null) {
            return;
        }
        for (TestScript.Test t : this.testScript.getTests()) {
            this.testData.addRow(this.createRow(t));
        }
        this.pack();
    }

    protected String[] createRow(TestScript.Test t) {
        String[] stringArray = new String[5];
        stringArray[0] = t.getFileName();
        stringArray[1] = t.isCompleted() ? (t.error() ? "Error" : "Successful") : "Not Run";
        stringArray[2] = t.getReturnValue().toString();
        stringArray[3] = "";
        stringArray[4] = "";
        String[] out = stringArray;
        if (t.isCompleted()) {
            out[3] = t.getActualReturnValue().toString();
            out[4] = t.isIoSuccessful() ? "Good" : "Error";
        }
        return out;
    }

    protected synchronized void runTests() {
        List<TestScript.Test> testsToRun;
        List<TestScript.Test> availableTests = this.testScript.getTests();
        if (this.tests.getSelectedRow() == -1) {
            testsToRun = availableTests;
            this.testScript.clearTests();
        } else {
            int[] requested;
            testsToRun = new ArrayList<TestScript.Test>();
            int[] nArray = requested = this.tests.getSelectedRows();
            int n = requested.length;
            int n2 = 0;
            while (n2 < n) {
                int i = nArray[n2];
                TestScript.Test test = availableTests.get(i);
                test.clear();
                testsToRun.add(test);
                ++n2;
            }
        }
        this.updateTable();
        this.statusBar.setPermanentText("Running Tests...");
        this.setStatus(4);
        this.testThread = new TestScript.TestThread(this, new Sys(), testsToRun);
        this.testThread.start();
    }

    protected synchronized void stopTests() {
        if (this.testThread != null) {
            this.testThread.interrupt();
        }
    }

    @Override
    public void threadEvent(int code, Object o) {
        switch (code) {
            case 4: {
                this.updateTestStatus();
                break;
            }
            case 0: {
                this.statusBar.setText("Tests Interrupted");
                this.setStatus(3);
                break;
            }
            case 2: {
                this.statusBar.setText("Tests Completed");
                this.setStatus(3);
                break;
            }
            case 1: {
                this.statusBar.setText("Tests Failed");
                this.setStatus(3);
                this.error("Error running tests: " + ((TestScript.TestScriptException)o).getMessage());
                break;
            }
        }
    }

    protected void updateTestStatus() {
        try {
            SwingUtilities.invokeAndWait(new Runnable(){

                @Override
                public void run() {
                    SamTester.this.updateTable();
                }
            });
        }
        catch (InterruptedException interruptedException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
    }

    protected void openTestDetails(int row) {
        if (row < 0) {
            return;
        }
        final TestScript.Test t = this.testScript.getTests().get(row);
        final JFrame f = new JFrame();
        JPanel p = new JPanel();
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setText(this.getInformation(t, false));
        info.setBackground(p.getBackground());
        f.setTitle(new File(t.getFileName()).getName());
        f.getContentPane().add(p);
        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.insets = new Insets(5, 5, 5, 5);
        p.setLayout(l);
        GridBagUtils.addLabel("Test Information:", p, l, c, 0, 0, 2, 1, 1.0, 1.0);
        GridBagUtils.addComponent(info, p, l, c, 0, 1, 2, 1, 1.0, 1.0);
        c.fill = 11;
        JButton guiButton = GridBagUtils.addButton("Launch in GUI", p, l, c, 0, 2, 1, 1, 1.0, 1.0);
        guiButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SamGUI.startUI(t.getFile().getAbsolutePath());
            }
        });
        JButton closeButton = GridBagUtils.addButton("Close", p, l, c, 1, 2, 1, 1, 1.0, 1.0);
        closeButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });
        closeButton.setSize(guiButton.getSize());
        f.setSize(300, 200);
        f.pack();
        f.setVisible(true);
    }

    protected String getInformation(TestScript.Test t, boolean html) {
        StringWriter outS = new StringWriter();
        PrintWriter out = new PrintWriter(outS);
        String ending = "";
        if (html) {
            ending = "<br>";
        }
        if (html) {
            out.println("<html>");
        }
        out.println("Name: " + t.getFileName() + ending);
        out.println("Status: " + (!t.isCompleted() ? "Not Run" : (t.error() ? "Error" : "Completed Successfully")) + ending);
        out.println("Expected Return Value: " + t.getReturnValue() + ending);
        if (t.getActualReturnValue() != null) {
            out.println("Actual Return Value: " + t.getActualReturnValue() + ending);
        }
        if (t.isCompleted()) {
            out.println("I/O  Complete: " + t.isIoSuccessful() + ending);
            out.println("Stack Cleared: " + t.isIoSuccessful() + ending);
        }
        if (html) {
            out.println("</html>");
        }
        return outS.toString();
    }

    protected void addTest() {
        TestScript.Test t = AddTestDialog.getNewTest(this.testScript, this);
        if (t != null) {
            this.testScript.getTests().add(t);
            this.modified = true;
            this.updateTable();
            if (this.testScript.getTests().size() > 0) {
                this.setStatus(3);
            }
        }
    }

    protected void deleteTest() {
        if (this.tests.getSelectedRow() == -1) {
            this.warning("You must select at least one row");
            return;
        }
        int[] toDelete = this.tests.getSelectedRows();
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected tests?", "Confirm", 0, 3) == 0) {
            int[] nArray = toDelete;
            int n = toDelete.length;
            int n2 = 0;
            while (n2 < n) {
                int i = nArray[n2];
                TestScript.Test t = this.testScript.getTests().get(i);
                t.delete();
                ++n2;
            }
            this.testScript.deleteTests();
        }
        this.modified = true;
        this.updateTable();
    }

    protected void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 0);
    }

    protected void warning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", 2);
    }

    protected void setStatus(int status) {
        switch (status) {
            case 1: {
                this.setButtons(true, false, false, false, false);
                break;
            }
            case 2: {
                this.setButtons(true, true, false, false, false);
                break;
            }
            case 3: {
                this.setButtons(true, true, true, true, false);
                break;
            }
            case 4: {
                this.setButtons(false, false, false, false, true);
            }
        }
    }

    protected void setButtons(boolean fileOps, boolean addTests, boolean delTests, boolean run, boolean stop) {
        this.newFileMenuItem.setEnabled(fileOps);
        this.openButton.setEnabled(fileOps);
        this.openFileMenuItem.setEnabled(fileOps);
        this.runButton.setEnabled(run);
        this.runRunMenuItem.setEnabled(run);
        this.stopButton.setEnabled(stop);
        this.stopRunMenuItem.setEnabled(stop);
        this.addTestsMenuItem.setEnabled(addTests);
        this.deleteTestsEnabled = delTests;
        this.deleteTestsMenuItem.setEnabled(this.deleteTestsEnabled && this.tests.getSelectedRow() != -1);
        this.saveFileMenuItem.setEnabled(fileOps && delTests);
        this.saveAsFileMenuItem.setEnabled(fileOps && delTests);
        if (run) {
            this.runButton.setBackground(new Color(204, 255, 204));
        } else {
            this.runButton.setBackground(new Color(204, 220, 204));
        }
        if (stop) {
            this.stopButton.setBackground(new Color(255, 204, 204));
        } else {
            this.stopButton.setBackground(new Color(220, 204, 204));
        }
        if (fileOps) {
            this.openButton.setBackground(new Color(204, 204, 255));
        } else {
            this.openButton.setBackground(new Color(204, 204, 220));
        }
    }

    protected void setWindowListeners() {
        this.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent e) {
            }
        });
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                SamTester.this.close();
            }
        });
    }

    protected boolean confirmClose() {
        if (!this.modified) {
            return true;
        }
        int ret = JOptionPane.showConfirmDialog(this, "Save file before closing?", "Confirm", 1, 3);
        if (ret == 0) {
            return this.saveScript(false);
        }
        return ret == 1;
    }

    @Override
    public boolean close() {
        if (!this.confirmClose()) {
            return false;
        }
        if (this.aboutDialog != null) {
            this.aboutDialog.dispose();
        }
        RegistrationSystem.unregister(classID, this);
        this.dispose();
        return true;
    }

    @Override
    public SamAboutDialog getAboutDialog() {
        if (this.aboutDialog == null) {
            this.aboutDialog = new SamAboutDialog("SaM", "2.6.2", "SaM Tester", this);
        }
        return this.aboutDialog;
    }

    public static void startUI() {
        SamTester.startUI(null);
    }

    public FileDialogManager getFileDialog() {
        return this.fileDialogs;
    }

    public static void startUI(String filename) {
        SamTester gui = new SamTester();
        gui.start();
        if (filename != null) {
            gui.loadFile(new File(filename));
        } else {
            gui.newScript();
        }
    }

    protected static class AddTestDialog
    extends JDialog {
        protected JTextField fileTextField;
        protected File testFile;
        protected JTextField returnValueTextField;
        protected JComboBox returnValueTypeList;
        protected Object returnValue;
        protected JList readList;
        protected JList writeList;
        protected JButton readAdd;
        protected JButton readDelete;
        protected JButton writeAdd;
        protected JButton writeDelete;
        protected List<Object> write;
        protected List<Object> read;
        protected static final int READ = 1;
        protected static final int WRITE = 2;
        protected TestScript testScript;
        protected boolean validTest = false;

        protected AddTestDialog(TestScript ts, SamTester parent) {
            super((Frame)parent, true);
            this.testScript = ts;
            this.getContentPane().add(this.createComponents(parent));
            this.setTitle("Add Test");
            this.pack();
        }

        protected JPanel createComponents(SamTester parent) {
            JPanel p = new JPanel();
            p.setPreferredSize(new Dimension(350, 300));
            p.setMinimumSize(new Dimension(250, 300));
            GridBagLayout l = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.fill = 2;
            c.insets = new Insets(5, 5, 5, 5);
            p.setLayout(l);
            int height = 0;
            GridBagUtils.addLabel("Enter Test Information:", p, l, c, 0, height, 3, 1, 0.0, 0.0);
            this.addFileInput(p, l, c, parent, ++height);
            this.addReturnValue(p, l, c, ++height);
            this.addReadSchedule(p, l, c, ++height);
            this.addWriteSchedule(p, l, c, ++height);
            ++height;
            JPanel bottomPanel = this.createBottomPanel();
            GridBagUtils.addComponent(bottomPanel, p, l, c, 0, 5, 3, 1, 0.0, 0.0);
            return p;
        }

        protected void addFileInput(JPanel p, GridBagLayout l, GridBagConstraints c, final SamTester parent, int height) {
            GridBagUtils.addLabel("File:", p, l, c, 0, height, 1, 1, 0.0, 0.0);
            this.fileTextField = new JTextField(30);
            this.fileTextField.setEditable(false);
            this.fileTextField.setEnabled(true);
            GridBagUtils.addComponent(this.fileTextField, p, l, c, 1, height, 1, 1, 1.0, 1.0);
            JButton browseButton = GridBagUtils.addButton("Browse", p, l, c, 2, height, 1, 1, 0.2, 0.0);
            browseButton.setPreferredSize(new Dimension(75, 30));
            browseButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    File cdir = testScript == null || testScript.getSourceFile() == null ? null : testScript.getSourceFile().getParentFile();
                    File f = parent.getFileDialog().getOpenFile(parent, testFileExtension, testFileTypeName, cdir, 2);
                    if (f == null) {
                        return;
                    }
                    if (cdir != null && f.getParent().equals(cdir.getName())) {
                        fileTextField.setText(f.getName());
                    } else {
                        fileTextField.setText(f.getAbsolutePath());
                    }
                    testFile = f;
                }
            });
        }

        public static TestScript.Test getNewTest(TestScript testScript, SamTester parent) {
            AddTestDialog d = new AddTestDialog(testScript, parent);
            d.setVisible(true);
            if (d.validTest) {
                TestScript.Test t = new TestScript.Test(d.testFile.toString());
                t.setReturnValue(d.returnValue);
                t.setScriptFile(d.testScript);
                t.setRead(d.read == null ? new ArrayList() : d.read);
                t.setWrite(d.write == null ? new ArrayList() : d.write);
                return t;
            }
            return null;
        }

        protected void addReturnValue(JPanel p, GridBagLayout l, GridBagConstraints c, int height) {
            GridBagUtils.addLabel("Return Value:", p, l, c, 0, height, 1, 1, 0.0, 0.0);
            this.returnValueTextField = new JTextField(10);
            GridBagUtils.addComponent(this.returnValueTextField, p, l, c, 1, height, 1, 1, 1.0, 1.0);
            this.returnValueTypeList = AddTestDialog.newTypeList(false);
            GridBagUtils.addComponent(this.returnValueTypeList, p, l, c, 2, height, 1, 1, 0.2, 0.0);
        }

        protected void addReadSchedule(JPanel p, GridBagLayout l, GridBagConstraints c, int height) {
            GridBagUtils.addLabel("Scheduled to read:", p, l, c, 0, height, 1, 1, 0.0, 0.0);
            this.readList = this.newIOList(1);
            this.read = new ArrayList<Object>();
            GridBagUtils.addComponent(this.scrollList(this.readList), p, l, c, 1, height, 1, 1, 1.0, 1.0);
            GridBagUtils.addComponent(this.newIOButtonPanel(1), p, l, c, 2, height, 1, 1, 0.2, 0.0);
        }

        protected void addWriteSchedule(JPanel p, GridBagLayout l, GridBagConstraints c, int height) {
            GridBagUtils.addLabel("Expected to write:", p, l, c, 0, height, 1, 1, 0.0, 0.0);
            this.writeList = this.newIOList(2);
            this.write = new ArrayList<Object>();
            GridBagUtils.addComponent(this.scrollList(this.writeList), p, l, c, 1, height, 1, 1, 1.0, 1.0);
            GridBagUtils.addComponent(this.newIOButtonPanel(2), p, l, c, 2, height, 1, 1, 0.2, 0.0);
        }

        protected static JComboBox newTypeList(boolean string) {
            if (string) {
                return new JComboBox<Object>(EnumSet.allOf(IOType.class).toArray());
            }
            return new JComboBox<Object>(EnumSet.range(IOType.INTEGER, IOType.CHAR).toArray());
        }

        protected JList newIOList(int io) {
            JList l = new JList();
            l.setSelectionMode(0);
            l.setModel(new DefaultListModel());
            if (io == 1) {
                l.addListSelectionListener(new ListSelectionListener(){

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        JList source = (JList)e.getSource();
                        if (source.getSelectedIndex() == -1) {
                            readDelete.setEnabled(false);
                        } else {
                            readDelete.setEnabled(true);
                        }
                    }
                });
            } else if (io == 2) {
                l.addListSelectionListener(new ListSelectionListener(){

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        JList source = (JList)e.getSource();
                        if (source.getSelectedIndex() == -1) {
                            writeDelete.setEnabled(false);
                        } else {
                            writeDelete.setEnabled(true);
                        }
                    }
                });
            }
            return l;
        }

        protected JScrollPane scrollList(JList l) {
            JScrollPane p = new JScrollPane(l);
            p.setPreferredSize(new Dimension(100, 60));
            p.setMinimumSize(new Dimension(100, 60));
            return p;
        }

        protected JPanel createBottomPanel() {
            JPanel p = new JPanel();
            GridBagLayout l = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.fill = 2;
            c.insets = new Insets(5, 5, 5, 5);
            p.setLayout(l);
            JButton addTestButton = GridBagUtils.addButton("Add Test", p, l, c, 0, 0, 1, 1, 0.0, 0.0);
            addTestButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (testFile == null) {
                        this.error("You must select a file for this test.");
                        return;
                    }
                    if (returnValueTextField.getText().length() == 0) {
                        this.error("You must fill in a return value.");
                        return;
                    }
                    returnValue = AddTestDialog.createObject(returnValueTextField.getText(), (IOType)((Object)returnValueTypeList.getSelectedItem()));
                    if (returnValue == null) {
                        this.error("You must fill in a valid return value for the type you selected.");
                        return;
                    }
                    validTest = true;
                    this.dispose();
                }
            });
            JButton cancelButton = GridBagUtils.addButton("Cancel", p, l, c, 1, 0, 1, 1, 0.0, 0.0);
            cancelButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    this.dispose();
                }
            });
            return p;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected static Object createObject(String s, IOType type) {
            String ret = null;
            try {
                switch (type) {
                    case INTEGER: {
                        return new Integer(s);
                    }
                    case FLOAT: {
                        return new Float(s);
                    }
                    case CHAR: {
                        if (s.length() == 1) return new Character(s.charAt(0));
                        return null;
                    }
                    case STRING: {
                        return s;
                    }
                }
                return ret;
            }
            catch (NumberFormatException e) {
                return null;
            }
        }

        private void error(String message) {
            JOptionPane.showMessageDialog(this, message, "Error", 0);
        }

        protected JPanel newIOButtonPanel(int io) {
            JPanel p = new JPanel();
            p.setMaximumSize(new Dimension(75, 60));
            p.setMinimumSize(new Dimension(75, 60));
            GridBagLayout l = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.fill = 2;
            p.setLayout(l);
            c.insets = new Insets(0, 0, 2, 0);
            JButton addIOButton = GridBagUtils.addButton("Add", p, l, c, 0, 0, 1, 1, 1.0, 1.0);
            c.insets = new Insets(3, 0, 0, 0);
            JButton delIOButton = GridBagUtils.addButton("Delete", p, l, c, 0, 1, 1, 1, 1.0, 1.0);
            delIOButton.setEnabled(false);
            if (io == 1) {
                this.readAdd = addIOButton;
                this.readDelete = delIOButton;
                addIOButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        this.addIO(1);
                    }
                });
                delIOButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        this.deleteIO(1);
                    }
                });
            } else if (io == 2) {
                this.writeAdd = addIOButton;
                this.writeDelete = delIOButton;
                addIOButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        this.addIO(2);
                    }
                });
                delIOButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        this.deleteIO(2);
                    }
                });
            }
            return p;
        }

        protected void addIO(int io) {
            Object o = AddIODialog.getIO(this);
            if (o != null) {
                if (io == 1) {
                    this.read.add(o);
                    this.updateIOList(this.read, this.readList);
                } else if (io == 2) {
                    this.write.add(o);
                    this.updateIOList(this.write, this.writeList);
                }
            }
        }

        protected void updateIOList(List<?> ioEntries, JList ioList) {
            DefaultListModel ioData = (DefaultListModel)ioList.getModel();
            ioData.clear();
            for (Object o : ioEntries) {
                ioData.addElement(o);
            }
        }

        protected void deleteIO(int io) {
            List<Object> data;
            JList l;
            if (io == 1) {
                l = this.readList;
                data = this.read;
            } else if (io == 2) {
                l = this.writeList;
                data = this.write;
            } else {
                return;
            }
            if (l.getSelectedIndex() == -1) {
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected I/O?", "Confirm", 0, 3) == 0) {
                data.remove(l.getSelectedIndex());
                this.updateIOList(data, l);
            }
        }

        private static class AddIODialog
        extends JDialog {
            protected JTextField valueField;
            protected JComboBox typeField;
            protected Object io = null;

            protected AddIODialog(AddTestDialog parent) {
                super((Dialog)parent, true);
                Container p = this.getContentPane();
                this.setTitle("Add I/O");
                GridBagLayout l = new GridBagLayout();
                GridBagConstraints c = new GridBagConstraints();
                c.fill = 2;
                c.insets = new Insets(5, 5, 5, 5);
                p.setLayout(l);
                this.valueField = new JTextField(10);
                GridBagUtils.addComponent(this.valueField, p, l, c, 0, 0, 1, 1, 1.0, 1.0);
                this.typeField = AddTestDialog.newTypeList(true);
                GridBagUtils.addComponent(this.typeField, p, l, c, 1, 0, 1, 1, 1.0, 1.0);
                GridBagUtils.addComponent(this.createBottomPanel(), p, l, c, 0, 1, 2, 1, 1.0, 1.0);
                this.pack();
            }

            public static Object getIO(AddTestDialog parent) {
                AddIODialog d = new AddIODialog(parent);
                d.setVisible(true);
                return d.io;
            }

            protected void error(String message) {
                JOptionPane.showMessageDialog(this, message, "Error", 0);
            }

            protected JPanel createBottomPanel() {
                JPanel p = new JPanel();
                GridBagLayout l = new GridBagLayout();
                GridBagConstraints c = new GridBagConstraints();
                c.fill = 2;
                c.insets = new Insets(5, 5, 5, 5);
                p.setLayout(l);
                JButton addIOButton = GridBagUtils.addButton("Add", p, l, c, 0, 0, 1, 1, 0.0, 0.0);
                addIOButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        io = AddTestDialog.createObject(valueField.getText(), (IOType)((Object)typeField.getSelectedItem()));
                        if (io != null) {
                            this.dispose();
                            return;
                        }
                        this.error("You must provide a valid value for this type");
                    }
                });
                JButton cancelButton = GridBagUtils.addButton("Cancel", p, l, c, 1, 0, 1, 1, 0.0, 0.0);
                cancelButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        this.dispose();
                    }
                });
                return p;
            }
        }

        protected static enum IOType {
            INTEGER("Integer"),
            FLOAT("Floating Point"),
            CHAR("Character"),
            STRING("String");

            private String name;

            private IOType(String name) {
                this.name = name;
            }

            public String toString() {
                return this.name;
            }
        }
    }
}

