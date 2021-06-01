package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STScrollBar;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class SwingScrollBar extends SwingBaseView implements STScrollBar {

    private final JScrollBar mBar;

    private ScrollListener mListener;

    public SwingScrollBar(int direction, int min , int max) {
         mBar = new JScrollBar(
                ((direction == STScrollBar.VERTICAL) ? JScrollBar.VERTICAL : JScrollBar.HORIZONTAL),
                min, 10, min, max);
    }

    @Override
    public void setScrollListener(ScrollListener listener) {
        mListener = listener;
        mBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                mListener.onValueChanged(ae.getValue());
            }
        });
    }

    @Override
    public Object getRealComponent() {
        return mBar;
    }

    @Override
    public void repaint() {

    }
}
