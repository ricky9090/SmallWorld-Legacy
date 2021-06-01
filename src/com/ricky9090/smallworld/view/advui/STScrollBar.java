package com.ricky9090.smallworld.view.advui;

public class STScrollBar extends STView {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private ScrollListener mListener;

    public void setScrollListener(ScrollListener listener) {
        mListener = listener;
    }

    public interface ScrollListener {
        void onValueChanged(int value);
    }
}
