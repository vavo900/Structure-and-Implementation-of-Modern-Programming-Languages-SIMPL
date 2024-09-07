/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.border.SoftBevelBorder;

public class StatusBar
extends Box {
    private JLabel label = new JLabel();
    private LabelThread curThread;

    public StatusBar() {
        super(0);
        this.setBorder(new SoftBevelBorder(1));
        this.add(this.label);
        this.label.setText(" ");
    }

    public synchronized void setText(String s) {
        if (this.curThread != null) {
            this.curThread.interrupt();
        }
        this.label.setText(s);
        this.curThread = new LabelThread(this);
        this.curThread.start();
    }

    public synchronized void setPermanentText(String s) {
        if (this.curThread != null) {
            this.curThread.interrupt();
        }
        this.label.setText(s);
        this.curThread = null;
    }

    public synchronized void clearText() {
        this.label.setText(" ");
    }

    class LabelThread
    extends Thread {
        private StatusBar statusBar;

        LabelThread(StatusBar bar) {
            this.statusBar = bar;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000L);
                this.statusBar.clearText();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }
}

