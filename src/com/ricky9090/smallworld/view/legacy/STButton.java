package com.ricky9090.smallworld.view.legacy;

public interface STButton extends STView {

    void setText(String text);

    void setButtonListener(ButtonListener listener);

    interface ButtonListener {
        void onClick();
    }
}
