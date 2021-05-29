package com.ricky9090.smallworld.display;

import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.*;

public interface IScreen {

    int POSITION_CENTER = 0;
    int POSITION_TOP = 1;
    int POSITION_RIGHT = 2;
    int POSITION_BOTTOM = 3;
    int POSITION_LEFT = 4;

    STWindow createWindow();

    STPanel createPanel(String content);

    STButton createButton(String text);

    STTextField createTextLine();

    STTextArea createTextArea();

    STGridPanel createGridPanel(int rows, int cols);

    STListView createListPanel(SmallObject[] dataArray);

    STMenu createMenu(String title);

    STMenuItem createMenuItem(String text);

    STBorderPanel createBorderPanel();

    STScrollBar createScrollBar(int direction, int min , int max);

    void showToast(String msg);

}
