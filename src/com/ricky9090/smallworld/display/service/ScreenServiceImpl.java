package com.ricky9090.smallworld.display.service;

import com.ricky9090.smallworld.SmallWorld;
import com.ricky9090.smallworld.display.IScreenService;
import com.ricky9090.smallworld.display.IScreenClient;
import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.UIConst;
import com.ricky9090.smallworld.view.advui.*;

import java.util.Arrays;

public class ScreenServiceImpl implements IScreenService {

    IScreenClient mClient;

    @Override
    public void bindClient(IScreenClient client) {
        mClient = client;
    }

    @Override
    public STWindow createWindow() {
        return new STWindow();
    }

    @Override
    public STLabelPanel createPanel(String content) {
        STLabelPanel panel = new STLabelPanel();
        panel.setText(content);
        return panel;
    }

    @Override
    public STButton createButton(String text) {
        STButton button = new STButton();
        button.setText(text);
        return button;
    }

    @Override
    public STTextField createTextLine() {
        STTextField textField = new STTextField();
        return textField;
    }

    @Override
    public STTextArea createTextArea() {
        STTextArea textArea = new STTextArea();
        return textArea;
    }

    @Override
    public STGridPanel createGridPanel(int rows, int cols) {
        STGridPanel gridPanel = new STGridPanel();
        gridPanel.setRows(rows);
        gridPanel.setCols(cols);
        return gridPanel;
    }

    @Override
    public STListView createListPanel(SmallObject[] dataArray) {
        STListView listView = new STListView();
        listView.setDataList(Arrays.asList(dataArray));
        return listView;
    }

    @Override
    public STMenu createMenu(String title) {
        STMenu menu = new STMenu();
        menu.setText(title);
        return menu;
    }

    @Override
    public STMenuItem createMenuItem(String text) {
        STMenuItem menuItem = new STMenuItem();
        menuItem.setText(text);
        return menuItem;
    }

    @Override
    public STBorderPanel createBorderPanel() {
        return new STBorderPanel();
    }

    @Override
    public STScrollBar createScrollBar(int direction, int min, int max) {
        return new STScrollBar();
    }

    @Override
    public void showToast(String msg) {
        System.out.println(msg);
    }

    @Override
    public void commit(STView dirtyTarget, int action) {
        synchronized (SmallWorld.RENDER_LOCK) {
            try {
                if (action == UIConst.PRIM_60_CREATE_WINDOW) {
                    mClient.commit(dirtyTarget, action);
                } else if (action == UIConst.PRIM_61_CHANGE_WINDOW_VISIBILITY ||
                        action == UIConst.PRIM_66_REPAINT_WINDOW) {
                    /*
                        Commit only when window is showing
                        since there is no view tree modifying,
                        let client finish creating window in one method
                    */
                    STWindow window = (STWindow) dirtyTarget;
                    if (window.isVisible()) {
                        mClient.commit(dirtyTarget, action);
                        System.out.println("Screen Service commit " + action);
                        SmallWorld.RENDER_LOCK.notifyAll();
                    }
                } else if (action == UIConst.PRIM_84_SET_LIST_DATA) {
                    mClient.commit(dirtyTarget, action);
                    System.out.println("Screen Service commit " + action);
                    SmallWorld.RENDER_LOCK.notifyAll();
                } else if (action == UIConst.PRIM_82_SET_VIEW_TEXT) {
                    mClient.commit(dirtyTarget, action);
                    System.out.println("Screen Service commit " + action);
                    SmallWorld.RENDER_LOCK.notifyAll();
                } else {
                    mClient.commit(dirtyTarget, action);
                    System.out.println("Screen Service commit " + action);
                    SmallWorld.RENDER_LOCK.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
