package com.ricky9090.smallworld.view.advui;

public class STLabelPanel extends STViewGroup {

    private final STTextView textView = new STTextView();

    public STTextView getTextView() {
        return textView;
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
