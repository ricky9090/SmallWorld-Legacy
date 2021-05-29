package com.ricky9090.smallworld.view;

public interface STTextView extends STView {

    String getText();

    String getSelectedText();

    void setText(String text);

    void replaceSelectedText(String text);
}
