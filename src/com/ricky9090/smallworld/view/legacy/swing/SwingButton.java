package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingButton extends SwingBaseView implements STButton {

    private final JButton mButton;
    private ButtonListener mListener;

    public SwingButton() {
        mButton = new JButton();
    }

    public SwingButton(String text) {
        mButton = new JButton(text);
    }

    @Override
    public void setText(String text) {
        mButton.setText(text);
    }

    @Override
    public void setButtonListener(ButtonListener listener) {
        mListener = listener;
        mButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });
    }

    @Override
    public Object getRealComponent() {
        return mButton;
    }

    @Override
    public void repaint() {

    }
}
