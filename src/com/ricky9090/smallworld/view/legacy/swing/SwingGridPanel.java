package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STGridPanel;
import com.ricky9090.smallworld.view.legacy.STView;

import javax.swing.*;
import java.awt.*;

public class SwingGridPanel extends SwingBaseView implements STGridPanel {

    private final JPanel mPanel;

    public SwingGridPanel() {
        mPanel = new JPanel();
        mPanel.setLayout(new GridLayout());
    }

    public SwingGridPanel(int rows, int cols) {
        mPanel = new JPanel();
        mPanel.setLayout(new GridLayout(rows, cols));
    }

    @Override
    public void addChildView(STView view) {
        mPanel.add((Component) view.getRealComponent());
    }

    @Override
    public Object getRealComponent() {
        return mPanel;
    }

    @Override
    public void repaint() {

    }
}
