package com.ricky9090.smallworld.view.impl.swing;

import com.ricky9090.smallworld.view.STImageView;
import com.ricky9090.smallworld.view.STPanel;
import com.ricky9090.smallworld.view.STView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingPanel extends SwingBaseView implements STPanel {

    private final JScrollPane mPanel;
    private JLabel mLabel;
    private InputEventListener inputEventListener;

    public SwingPanel() {
        mLabel = new JLabel();
        mPanel =  new JScrollPane(mLabel);
    }

    public SwingPanel(String content) {
        mLabel = new JLabel(content);
        mPanel =  new JScrollPane(mLabel);
    }

    @Override
    public void setContent(String content) {
        ((JLabel) mPanel.getViewport().getView()).setText(content);
    }

    @Override
    public void setImage(STImageView imageView) {
        mLabel.setIcon(new ImageIcon((Image) imageView.getRealComponent()));
        mLabel.setHorizontalAlignment(SwingConstants.LEFT);
        mLabel.setVerticalAlignment(SwingConstants.TOP);
        mLabel.repaint();
    }

    @Override
    public Object getRealComponent() {
        return mPanel;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void addInputEventListener(InputEventListener listener) {
        inputEventListener = listener;
        mLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (inputEventListener != null) {
                    inputEventListener.onInputEvent(STView.EVENT_DOWN, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (inputEventListener != null) {
                    inputEventListener.onInputEvent(STView.EVENT_UP, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (inputEventListener != null) {
                    inputEventListener.onInputEvent(STView.EVENT_MOVE, e.getX(), e.getY());
                }
            }
        });
    }
}
