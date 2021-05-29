package com.ricky9090.smallworld.view.impl.swing;

import com.ricky9090.smallworld.view.STTextArea;

import javax.swing.*;

public class SwingTextArea extends SwingBaseView implements STTextArea {

    private final JScrollPane mAreaPane;
    private JTextArea mTextArea;

    public SwingTextArea() {
        mTextArea = new JTextArea();
        mTextArea.setTabSize(4);
        mAreaPane = new JScrollPane(mTextArea);
    }

    @Override
    public Object getRealComponent() {
        return mAreaPane;
    }

    @Override
    public void repaint() {

    }

    @Override
    public String getText() {
        return mTextArea.getText();
    }

    @Override
    public String getSelectedText() {
        return mTextArea.getSelectedText();
    }

    @Override
    public void setText(String text) {
        mTextArea.setText(text);
    }

    @Override
    public void replaceSelectedText(String text) {
        mTextArea.replaceSelection(text);
    }
}
