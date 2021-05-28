package com.ricky9090.smallworld.display;

public interface IScreen {

    int POSITION_CENTER = 0;
    int POSITION_TOP = 1;
    int POSITION_RIGHT = 2;
    int POSITION_BOTTOM = 3;
    int POSITION_LEFT = 4;

    Object createWindow();

    Object createPanel(String content);

    Object createButton(String text, ButtonListener listener);

    Object createTextLine();

    Object createTextArea();

    Object createGridPanel(Object[] dataArray, int rows, int cols);

    Object createListPanel(Object[] dataArray, ListListener listener);

    Object createMenu(String title);

    void addMenuItem(Object targetMenu, String text, ButtonListener listener);

    Object createBorderPanel();

    void addToBorder(Object targetBorder, int position, Object component);

    void addImageToLabel(Object target, Object img);

    void showToast(String msg);

    void showWindow(Object targetWindow);

    void hideWindow(Object targetWindow);

    void setWindowContent(Object targetWindow, Object content);

    void setWindowSize(Object targetWindow, int width, int height);

    void addMenu(Object targetWindow, Object targetMenu);

    void setWindowTitle(Object targetWindow, String title);

    void repaintWindow(Object targetWindow);

    void repaintComponent(Object targetComponent);

    String getText(Object target);

    String getSelectedText(Object target);

    void setText(Object target, String text);

    void replaceSelectedText(Object target, String text);

}
