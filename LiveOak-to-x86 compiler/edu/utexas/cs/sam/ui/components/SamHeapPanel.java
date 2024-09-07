/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui.components;

import edu.utexas.cs.sam.core.HeapAllocator;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.ui.components.MemoryCell;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.SoftBevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class SamHeapPanel
extends JPanel {
    private JScrollPane heapScrollPane;
    private JTree heap;

    public SamHeapPanel() {
        this.setPreferredSize(new Dimension(200, 350));
        this.setMinimumSize(new Dimension(100, 100));
        this.setLayout(new BorderLayout());
        this.heap = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("Heap")));
        this.heap.setCellRenderer(new HeapCellRenderer());
        this.heap.setRootVisible(false);
        ToolTipManager.sharedInstance().registerComponent(this.heap);
        this.heapScrollPane = new JScrollPane(this.heap);
        this.heapScrollPane.setBorder(new SoftBevelBorder(1));
        this.add((Component)new JLabel("Heap:"), "North");
        this.add((Component)this.heapScrollPane, "Center");
    }

    public void update(Memory mem) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Heap");
        DefaultMutableTreeNode current = null;
        HeapAllocator allocator = mem.getHeapAllocator();
        if (allocator == null) {
            return;
        }
        Iterator<HeapAllocator.Allocation> iter = allocator.getAllocations();
        while (iter.hasNext()) {
            HeapAllocator.Allocation alloc = iter.next();
            int addr = alloc.getAddr();
            int size = alloc.getSize();
            current = new DefaultMutableTreeNode("Allocation (Size: " + alloc.getSize() + ")");
            root.add(current);
            int i = 0;
            for (Memory.Data value : mem.getAllocation(alloc)) {
                current.add(new DefaultMutableTreeNode(new MemoryCell(value, addr + i++)));
            }
        }
        ((DefaultTreeModel)this.heap.getModel()).setRoot(root);
        ((DefaultTreeModel)this.heap.getModel()).reload();
        this.heapScrollPane.revalidate();
        this.heapScrollPane.repaint();
    }

    private class HeapCellRenderer
    extends DefaultTreeCellRenderer {
        Color defaultBackgroundNonSelectionColor = this.getBackgroundNonSelectionColor();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            this.setBackgroundNonSelectionColor(this.defaultBackgroundNonSelectionColor);
            super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
            if (!(value instanceof DefaultMutableTreeNode) || ((DefaultMutableTreeNode)value).getUserObject() == null || !(((DefaultMutableTreeNode)value).getUserObject() instanceof MemoryCell)) {
                return this;
            }
            MemoryCell cell = (MemoryCell)((DefaultMutableTreeNode)value).getUserObject();
            this.setText(cell.getText());
            this.setToolTipText(cell.getToolTip());
            this.setBackgroundNonSelectionColor(cell.getColor());
            return this;
        }
    }
}

