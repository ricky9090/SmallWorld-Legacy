package com.ricky9090.smallworld.view.advui;

public class STMenuItem extends STTextView {

    private MenuItemListener mListener;

    private final MenuItemListener foobarListener = new MenuItemListener() {
        @Override
        public void onClick() {

        }
    };

    public void addMenuItemListener(MenuItemListener listener) {
        mListener = listener;
    }

    public MenuItemListener getListener() {
        if (mListener == null) {
            return foobarListener;
        }
        return mListener;
    }

    public interface MenuItemListener {
        void onClick();
    }
}
