package com.ricky9090.smallworld.view.advui;

public class STTextView extends STView {

    private String mText;
    private String mSelectedText;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getSelectedText() {
        return mSelectedText;
    }

    public void setSelectedText(String selectedText) {
        this.mSelectedText = selectedText;
    }
}
