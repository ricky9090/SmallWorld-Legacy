package com.ricky9090.smallworld.view;

public interface STView {

    int EVENT_DOWN = 0;
    int EVENT_MOVE = 1;
    int EVENT_UP = 2;

    Object getRealComponent();

    void repaint();

    void addInputEventListener(InputEventListener listener);

    interface InputEventListener {
        void onInputEvent(int eventType, int x, int y);
    }
}
