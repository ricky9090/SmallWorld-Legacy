package com.ricky9090.smallworld.display.client;

import com.ricky9090.smallworld.SmallWorld;
import com.ricky9090.smallworld.display.IScreenClient;
import com.ricky9090.smallworld.display.IScreenService;
import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.UIConst;
import com.ricky9090.smallworld.view.advui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class SwingClientImpl extends Thread implements IScreenClient {

    public boolean running = true;
    private final IScreenService screenService;

    private final Map<String, WindowPair> localWindowMap = new HashMap<>();

    private final List<UIAction> uiActionList = new LinkedList<>();

    public SwingClientImpl(IScreenService service) {
        this.screenService = service;
        service.bindClient(this);
    }

    @Override
    public void run() {
        while (running) {
            try {
                synchronized (SmallWorld.RENDER_LOCK) {
                    while (uiActionList.size() == 0) {
                        SmallWorld.RENDER_LOCK.wait(20);
                    }

                    // dirty check
                    // update
                    onUpdate();

                    SmallWorld.RENDER_LOCK.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdate() {
        UIAction uiAction = uiActionList.remove(0);

        final STView dirtyTarget = uiAction.target;
        final int dirtyAction = uiAction.action;
        System.out.println("Swing Client on Update: " + dirtyAction);
        if (dirtyAction == UIConst.PRIM_60_CREATE_WINDOW) {
            STWindow window = (STWindow) dirtyTarget;
            WindowPair pair = new WindowPair();
            pair.stWindow = window;
            String tag = pair.stWindow.getId() + "";

            localWindowMap.put(tag, pair);
        } else if (dirtyAction == UIConst.PRIM_61_CHANGE_WINDOW_VISIBILITY) {
            // build real window only when the whole window has been completely created
            STWindow window = (STWindow) dirtyTarget;
            String windowTag = window.getId() + "";

            JDialog _swingWindow = buildSwingWindow(window);
            _swingWindow.setVisible(true);

            WindowPair pair = localWindowMap.get(windowTag);
            if (pair != null) {
                pair.swingWindow = _swingWindow;
            }

        } else if (dirtyAction == UIConst.PRIM_66_REPAINT_WINDOW) {
            STWindow window = (STWindow) dirtyTarget;
            String windowTag = window.getId() + "";
            WindowPair pair = localWindowMap.get(windowTag);
            if (pair != null && pair.swingWindow != null) {
                pair.swingWindow.repaint();
            }
        } else if (dirtyAction == UIConst.PRIM_84_SET_LIST_DATA) {
            String targetTag = dirtyTarget.getId() + "";
            WidgetPair targetPair = findWidgetPairInWindowMap(targetTag);

            if (targetPair != null) {
                try {

                    Component component = targetPair.swingWidget;
                    if (component == null) {
                        return;
                    }
                    Component listComponent = ((JScrollPane) component).getViewport().getView();
                    STListView targetListView = (STListView) targetPair.stWidget;
                    ((JList<Object>) listComponent).setListData(targetListView.getDataList().toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (dirtyAction == UIConst.PRIM_82_SET_VIEW_TEXT) {
            String targetTag = dirtyTarget.getId() + "";
            WidgetPair targetPair = findWidgetPairInWindowMap(targetTag);

            if (targetPair != null) {
                try {
                    Component component = targetPair.swingWidget;
                    if (component == null) {
                        return;
                    }
                    if (component instanceof JScrollPane) {
                        component = ((JScrollPane) component).getViewport().getView();
                    }
                    STTextView targetTextView = (STTextView) targetPair.stWidget;

                    if (component instanceof JTextField) {
                        ((JTextField) component).setText(targetTextView.getText());
                    } else if (component instanceof JTextArea) {
                        ((JTextArea) component).setText(targetTextView.getText());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private JDialog buildSwingWindow(STWindow window) {
        JDialog swingWindow = new JDialog();

        // size
        swingWindow.setSize(window.getWidth(), window.getHeight());

        // title
        swingWindow.setTitle(window.getTitle());

        // menu
        swingWindow.setJMenuBar(new JMenuBar());
        List<STMenu> menuList = window.getMenuList();
        for (STMenu menu : menuList) {
            JMenu swingMenu = buildMenu(window, menu);
            swingWindow.getJMenuBar().add(swingMenu);

            String widgetTag = menu.getId() + "";
            String windowTag = window.getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingMenu, menu);
            localWindowMap.get(windowTag).registerWidget(widgetTag, widgetPair);
        }

        // content
        Component content = buildContent(window, window.getContent());
        if (content != null) {
            swingWindow.getContentPane().add(content);
        }

        swingWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                //screenService.getWindowManager().switchToWindow(window.getId());
            }

            @Override
            public void windowClosing(WindowEvent e) {
                window.getListener().onWindowClosing();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                String tag = window.getId() + "";
                synchronized (SmallWorld.RENDER_LOCK) {
                    localWindowMap.remove(tag);
                }
            }
        });

        return swingWindow;
    }

    private JMenu buildMenu(STWindow window, STMenu menu) {
        JMenu swingMenu = new JMenu(menu.getText());
        List<STMenuItem> itemList = menu.getItemList();
        for (STMenuItem item : itemList) {
            JMenuItem swingMenuItem = buildMenuItem(item);
            swingMenu.add(swingMenuItem);

            String widgetTag = item.getId() + "";
            String windowTag = window.getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingMenuItem, item);

            localWindowMap.get(windowTag).registerWidget(widgetTag, widgetPair);
        }

        return swingMenu;
    }

    private JMenuItem buildMenuItem(STMenuItem menuItem) {
        JMenuItem swingMenu = new JMenuItem(menuItem.getText());
        swingMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuItem.getListener().onClick();
            }
        });
        return swingMenu;
    }

    private Component buildContent(STWindow window, STView content) {
        Component realWidget = dispatchBuildView(window, content);
        String widgetTag = content.getId() + "";
        String windowTag = window.getId() + "";

        WidgetPair widgetPair = new WidgetPair(realWidget, content);
        localWindowMap.get(windowTag).registerWidget(widgetTag, widgetPair);
        return realWidget;
    }

    private Component dispatchBuildView(STWindow window, STView view) {
        if (view instanceof STTextField) {
            return buildTextField((STTextField) view);
        }
        if (view instanceof STTextArea) {
            return buildTextArea((STTextArea) view);
        }
        if (view instanceof STLabelPanel) {
            return buildLabelPanel((STLabelPanel) view);
        }
        if (view instanceof STScrollBar) {
            return buildScrollBar((STScrollBar) view);
        }
        if (view instanceof STButton) {
            return buildButton((STButton) view);
        }
        if (view instanceof STListView) {
            return buildListView((STListView) view);
        }
        if (view instanceof STBorderPanel) {
            return buildBorderPanel(window, (STBorderPanel) view);
        }
        if (view instanceof STGridPanel) {
            return buildGridPanel(window, (STGridPanel) view);
        }
        return null;
    }

    private Component buildTextField(STTextField textField) {
        JTextField swingField = new JTextField();
        swingField.setText(textField.getText());
        swingField.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                String selected = swingField.getSelectedText();
                textField.setSelectedText(selected);
            }
        });
        return swingField;
    }

    private Component buildTextArea(STTextArea textArea) {
        JTextArea swingTextArea = new JTextArea();
        swingTextArea.setTabSize(4);
        swingTextArea.setText(textArea.getText());
        swingTextArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                String selected = swingTextArea.getSelectedText();
                textArea.setSelectedText(selected);
            }
        });
        return new JScrollPane(swingTextArea);
    }

    private Component buildLabelPanel(STLabelPanel labelPanel) {
        STTextView textView = labelPanel.getTextView();
        JLabel label = new JLabel();
        if (textView != null) {
            label.setText(textView.getText());
        }

        return new JScrollPane(label);
    }

    private Component buildScrollBar(STScrollBar scrollBar) {
        JLabel label = new JLabel("**ScrollBar**");
        return label;
    }

    private Component buildButton(STButton button) {
        JButton swingButton = new JButton();
        swingButton.setText(button.getText());
        swingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.getListener().onClick();
            }
        });
        return swingButton;
    }

    private Component buildListView(STListView listView) {
        JList<Object> swingList = new JList<>();
        swingList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        Object[] dataList = listView.getDataList().toArray();
        swingList.setListData(dataList);

        swingList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if ((!e.getValueIsAdjusting()) && (swingList.getSelectedIndex() >= 0)) {
                    int selectedIndex = swingList.getSelectedIndex();
                    listView.setSelectedIndex(selectedIndex);
                    listView.getListener().onListItemClick(selectedIndex, (SmallObject) swingList.getSelectedValue());
                }
            }
        });
        return new JScrollPane(swingList);
    }

    private Component buildBorderPanel(STWindow window, STBorderPanel borderPanel) {
        JPanel swingPanel = new JPanel();
        swingPanel.setLayout(new BorderLayout());

        String windowTag = window.getId() + "";

        if (borderPanel.getCenter() != null) {
            STView stCenter = borderPanel.getCenter();
            Component swingCenter = dispatchBuildView(window, stCenter);
            swingPanel.add("Center", swingCenter);
            String tag = borderPanel.getCenter().getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingCenter, stCenter);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getTop() != null) {
            STView stTop = borderPanel.getTop();
            Component swingTop = dispatchBuildView(window, stTop);
            swingPanel.add("North", swingTop);
            String tag = borderPanel.getTop().getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingTop, stTop);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getRight() != null) {
            STView stRight = borderPanel.getRight();
            Component swingRight = dispatchBuildView(window, stRight);
            swingPanel.add("East", swingRight);
            String tag = borderPanel.getRight().getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingRight, stRight);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getBottom() != null) {
            STView stBottom = borderPanel.getBottom();
            Component swingBottom = dispatchBuildView(window, stBottom);
            swingPanel.add("South", swingBottom);
            String tag = borderPanel.getBottom().getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingBottom, stBottom);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getLeft() != null) {
            STView stLeft = borderPanel.getLeft();
            Component swingLeft = dispatchBuildView(window, stLeft);
            swingPanel.add("West", swingLeft);
            String tag = borderPanel.getLeft().getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingLeft, stLeft);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        return swingPanel;
    }

    private Component buildGridPanel(STWindow window, STGridPanel gridPanel) {
        JPanel swingPanel = new JPanel();
        swingPanel.setLayout(new GridLayout(gridPanel.getRows(), gridPanel.getCols()));

        List<STView> children = gridPanel.getChildren();
        for (STView child : children) {
            Component swingChild = dispatchBuildView(window, child);
            swingPanel.add(swingChild);
            String tag = child.getId() + "";
            String windowTag = window.getId() + "";

            WidgetPair widgetPair = new WidgetPair(swingChild, child);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        return swingPanel;
    }

    private WidgetPair findWidgetPairInWindowMap(String tag) {

        Set<Map.Entry<String, WindowPair>> entrySet = localWindowMap.entrySet();
        for (Map.Entry<String, WindowPair> entry : entrySet) {
            WindowPair windowPair = entry.getValue();
            if (windowPair != null) {
                WidgetPair result = windowPair.findWidgetPair(tag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public void commit(STView target, int action) {
        UIAction uiAction = new UIAction();
        uiAction.target = target;
        uiAction.action = action;
        uiActionList.add(uiAction);
    }

    public static class UIAction {
        public STView target;
        public int action;
    }

    public static class WindowPair {
        public Component swingWindow;
        public STView stWindow;

        // Flat widgetMap for fast searching view in the view tree
        final Map<String, WidgetPair> realWidgetMap = new HashMap<>();

        public void registerWidget(String tag, WidgetPair widgetPair) {
            realWidgetMap.put(tag, widgetPair);
        }

        public WidgetPair findWidgetPair(String tag) {
            return realWidgetMap.get(tag);
        }
    }

    public static class WidgetPair {
        public Component swingWidget;
        public STView stWidget;

        public WidgetPair(Component swingWidget, STView stWidget) {
            this.swingWidget = swingWidget;
            this.stWidget = stWidget;
        }
    }

}
