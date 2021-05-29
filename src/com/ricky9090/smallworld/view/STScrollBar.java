package com.ricky9090.smallworld.view;

public interface STScrollBar extends STView {

    int VERTICAL = 0;
    int HORIZONTAL = 1;

    void setScrollListener(ScrollListener listener);

    interface ScrollListener {
        void onValueChanged(int value);
    }
}
