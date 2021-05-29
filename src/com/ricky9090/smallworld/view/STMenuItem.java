package com.ricky9090.smallworld.view;

public interface STMenuItem extends STView {

    void addMenuItemListener(MenuItemListener listener);

    interface MenuItemListener {
        void onClick();
    }
}
