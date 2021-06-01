package com.ricky9090.smallworld.display;

import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.advui.*;

public interface IScreenService {

    void bindClient(IScreenClient client);

    STWindow createWindow();

    STLabelPanel createPanel(String content);

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

    void commit(STView dirtyTarget, int action);
}
