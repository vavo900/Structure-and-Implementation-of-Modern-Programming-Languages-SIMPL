/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FileDialogManager {
    private JFileChooser[] fileChooserDialog = null;

    public FileDialogManager(int n) {
        this.fileChooserDialog = new JFileChooser[n];
        int i = 0;
        while (i < n) {
            this.fileChooserDialog[i] = new JFileChooser(".");
            ++i;
        }
    }

    public File getOpenFile(Component parent, String extension, String filetype, File cdir, int n) {
        if (cdir != null) {
            this.fileChooserDialog[n].setCurrentDirectory(cdir);
        }
        this.fileChooserDialog[n].resetChoosableFileFilters();
        this.fileChooserDialog[n].setFileFilter(new SimpleFilter(extension, String.valueOf(filetype) + " (*." + extension + ")"));
        if (this.fileChooserDialog[n].showOpenDialog(parent) != 0) {
            return null;
        }
        return this.fileChooserDialog[n].getSelectedFile();
    }

    public File getSaveFile(Component parent, String extension, String filetype, File cdir, int n) {
        File file;
        if (cdir != null) {
            this.fileChooserDialog[n].setCurrentDirectory(cdir);
        }
        this.fileChooserDialog[n].resetChoosableFileFilters();
        this.fileChooserDialog[n].setFileFilter(new SimpleFilter(extension, String.valueOf(filetype) + " (*." + extension + ")"));
        block5: while (true) {
            if (this.fileChooserDialog[n].showSaveDialog(parent) != 0) {
                return null;
            }
            file = this.fileChooserDialog[n].getSelectedFile();
            if (!file.getName().endsWith("." + extension)) {
                file = new File(String.valueOf(file.getAbsolutePath()) + "." + extension);
            }
            if (!file.exists()) break;
            int r = JOptionPane.showConfirmDialog(parent, "File already exists. Overwrite?", "Warning", 1, 2);
            switch (r) {
                case 0: {
                    return file;
                }
                case 1: {
                    continue block5;
                }
                case 2: {
                    return null;
                }
            }
        }
        return file;
    }

    public File getOpenDirectory(Component parent, File cdir, int n) {
        if (cdir != null) {
            this.fileChooserDialog[n].setCurrentDirectory(cdir);
        }
        this.fileChooserDialog[n].resetChoosableFileFilters();
        this.fileChooserDialog[n].setFileSelectionMode(1);
        if (this.fileChooserDialog[n].showOpenDialog(parent) != 0) {
            return null;
        }
        return this.fileChooserDialog[n].getSelectedFile();
    }

    public class SimpleFilter
    extends FileFilter {
        private String description = null;
        private String extension = null;

        public SimpleFilter(String extension, String description) {
            this.description = description;
            this.extension = "." + extension.toLowerCase();
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public boolean accept(File f) {
            if (f == null) {
                return false;
            }
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().toLowerCase().endsWith(this.extension);
        }
    }
}

