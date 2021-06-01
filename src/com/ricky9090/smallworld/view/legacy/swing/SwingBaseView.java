package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STView;

public abstract class SwingBaseView implements STView {

    @Override
    public Object getRealComponent() {
        return null;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void addInputEventListener(InputEventListener listener) {

    }
}
