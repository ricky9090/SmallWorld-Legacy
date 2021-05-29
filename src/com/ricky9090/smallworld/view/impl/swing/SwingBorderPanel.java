package com.ricky9090.smallworld.view.impl.swing;

import com.ricky9090.smallworld.view.STBorderPanel;
import com.ricky9090.smallworld.view.STView;

import javax.swing.*;
import java.awt.*;

import static com.ricky9090.smallworld.display.IScreen.*;

public class SwingBorderPanel extends SwingBaseView implements STBorderPanel {

    private final JPanel mPanel;

    public SwingBorderPanel() {
        mPanel = new JPanel();
        mPanel.setLayout(new BorderLayout());
    }

    @Override
    public void addChild(int position, STView view) {
        Component realView = (Component) view.getRealComponent();
        switch (position) {
            case POSITION_CENTER:
                mPanel.add("Center", realView);
                break;
            case POSITION_TOP:
                mPanel.add("North", realView);
                break;
            case POSITION_RIGHT:
                mPanel.add("East", realView);
                break;
            case POSITION_BOTTOM:
                mPanel.add("South", realView);
                break;
            case POSITION_LEFT:
                mPanel.add("West", realView);
                break;
        }
    }

    @Override
    public Object getRealComponent() {
        return mPanel;
    }

    @Override
    public void repaint() {

    }
}
