package com.ricky9090.smallworld.view.impl.swing;

import com.ricky9090.smallworld.view.STMenu;
import com.ricky9090.smallworld.view.STMenuItem;

import javax.swing.*;

public class SwingMenu extends SwingBaseView implements STMenu {

    private final JMenu mMenu;

    public SwingMenu() {
        mMenu = new JMenu();
    }

    public SwingMenu(String title) {
        mMenu = new JMenu(title);
    }

    @Override
    public void setText(String text) {
        mMenu.setText(text);
    }

    @Override
    public void addItem(STMenuItem item) {
        mMenu.add((JMenuItem) item.getRealComponent());
    }

    @Override
    public Object getRealComponent() {
        return mMenu;
    }

    @Override
    public void repaint() {

    }
}
