package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STMenuItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingMenuItem extends SwingBaseView implements STMenuItem {

    private final JMenuItem mItem;

    private MenuItemListener mListener;

    public SwingMenuItem(String text) {
        mItem = new JMenuItem(text);
    }

    @Override
    public void addMenuItemListener(MenuItemListener listener) {
        mListener = listener;
        mItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });
    }

    @Override
    public Object getRealComponent() {
        return mItem;
    }

    @Override
    public void repaint() {
        mItem.repaint();
    }
}
