package com.ricky9090.smallworld.view.impl.swing;

import com.ricky9090.smallworld.view.STMenu;
import com.ricky9090.smallworld.view.STView;
import com.ricky9090.smallworld.view.STWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingWindow extends SwingBaseView implements STWindow {

    private final JDialog mWindow;

    private WindowListener mListener;

    public SwingWindow() {
        mWindow = new JDialog();
        mWindow.setVisible(false);
    }

    @Override
    public Object getRealComponent() {
        return mWindow;
    }

    @Override
    public void show() {
        mWindow.setVisible(true);
    }

    @Override
    public void hide() {
        mWindow.setVisible(false);
    }

    @Override
    public void setContent(STView content) {
        mWindow.getContentPane().add((Component) content.getRealComponent());
    }

    @Override
    public void setSize(int width, int height) {
        mWindow.setSize(width, height);
    }

    @Override
    public void addMenu(STMenu targetMenu) {
        if (mWindow.getJMenuBar() == null) {
            mWindow.setJMenuBar(new JMenuBar());
        }
        mWindow.getJMenuBar().add((JMenu) targetMenu.getRealComponent());
    }

    @Override
    public void setTitle(String title) {
        mWindow.setTitle(title);
    }

    @Override
    public void repaint() {
        mWindow.repaint();
    }

    @Override
    public void setWindowListener(WindowListener listener) {
        mListener = listener;
        mWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (mListener != null) {
                    mListener.onWindowClose();
                }
            }
        });
    }
}
