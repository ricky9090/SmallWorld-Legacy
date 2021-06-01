package com.ricky9090.smallworld.view.advui;

public class STButton extends STTextView {

    private ButtonListener mListener;
    private final ButtonListener foobarListener = new ButtonListener() {
        @Override
        public void onClick() {

        }
    };

    public void setListener(ButtonListener listener) {
        this.mListener = listener;
    }

    public ButtonListener getListener() {
        if (mListener == null) {
            return foobarListener;
        }
        return mListener;
    }

    public interface ButtonListener {
        void onClick();
    }
}
