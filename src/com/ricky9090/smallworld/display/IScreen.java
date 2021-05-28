package com.ricky9090.smallworld.display;

public interface IScreen {

    Object createWindow();

    Object createPanel(String content);

    void showToast(String msg);

    void showWindow(Object targetWindow);

    void hideWindow(Object targetWindow);

    void setWindowContent(Object targetWindow, Object content);

    void setWindowSize(Object targetWindow, int width, int height);

    void addMenu(Object targetWindow, Object targetMenu);

    void setWindowTitle(Object targetWindow, String title);

    void repaintWindow(Object targetWindow);

}
