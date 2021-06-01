package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STTextField;

import javax.swing.*;

public class SwingTextField extends SwingBaseView implements STTextField {

    private final JTextField mField;

    public SwingTextField() {
        this.mField =  new JTextField();
    }

    @Override
    public Object getRealComponent() {
        return mField;
    }

    @Override
    public void repaint() {

    }

    @Override
    public String getText() {
        return mField.getText();
    }

    @Override
    public String getSelectedText() {
        return mField.getSelectedText();
    }

    @Override
    public void setText(String text) {
        mField.setText(text);
    }

    @Override
    public void replaceSelectedText(String text) {
        mField.replaceSelection(text);
    }
}
