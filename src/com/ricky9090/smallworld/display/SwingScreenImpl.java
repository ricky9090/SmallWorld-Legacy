package com.ricky9090.smallworld.display;

import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.legacy.*;
import com.ricky9090.smallworld.view.legacy.swing.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class SwingScreenImpl implements IScreen {

    public SwingScreenImpl() {
    }

    private JDialog asWindow(Object target) {
        return (JDialog) target;
    }

    private JButton asButton(Object target) {
        return (JButton) target;
    }

    private JPanel asPanel(Object target) {
        return (JPanel) target;
    }

    private Component asComponent(Object target) {
        return (Component) target;
    }

    @Override
    public STWindow createWindow() {
        STWindow window = new SwingWindow();
        return window;
    }

    @Override
    public STPanel createPanel(String content) {
        return new SwingPanel(content);
    }

    @Override
    public STButton createButton(String text) {
        STButton button = new SwingButton(text);
        return button;
    }

    @Override
    public STTextField createTextLine() {
        return new SwingTextField();
    }

    @Override
    public STTextArea createTextArea() {
        return new SwingTextArea();
    }

    @Override
    public STGridPanel createGridPanel(int rows, int cols) {
        STGridPanel panel = new SwingGridPanel(rows, cols);
        return panel;
    }

    @Override
    public STListView createListPanel(SmallObject[] dataArray) {
        return new SwingListView(dataArray);
    }

    @Override
    public STMenu createMenu(String title) {
        return new SwingMenu(title);
    }

    @Override
    public STMenuItem createMenuItem(String text) {
        return new SwingMenuItem(text);
    }

    @Override
    public STBorderPanel createBorderPanel() {
        return new SwingBorderPanel();
    }

    @Override
    public STScrollBar createScrollBar(int direction, int min , int max) {
        return new SwingScrollBar(direction, min, max);
    }

    public void addToBorder(Object targetBorder, int position, Object comp) {
        if (targetBorder instanceof JPanel) {
            switch (position) {
                case POSITION_CENTER:
                    asPanel(targetBorder).add("Center", asComponent(comp));
                    break;
                case POSITION_TOP:
                    asPanel(targetBorder).add("North", asComponent(comp));
                    break;
                case POSITION_RIGHT:
                    asPanel(targetBorder).add("East", asComponent(comp));
                    break;
                case POSITION_BOTTOM:
                    asPanel(targetBorder).add("South", asComponent(comp));
                    break;
                case POSITION_LEFT:
                    asPanel(targetBorder).add("West", asComponent(comp));
                    break;
            }
        }
    }

    public void addImageToLabel(Object target, Object img) {
        Object tmp = target;
        if (tmp instanceof JScrollPane) {
            tmp = ((JScrollPane) tmp).getViewport().getView();
        }
        if (tmp instanceof JLabel) {
            JLabel label = (JLabel) tmp;
            label.setIcon(new ImageIcon((Image) img));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setVerticalAlignment(SwingConstants.TOP);
            label.repaint();
        }
    }

    @Override
    public void showToast(String msg) {
        JOptionPane.showMessageDialog(new JFrame("X"), msg);
    }

    public void setWindowContent(Object targetWindow, Object content) {
        if (targetWindow instanceof JDialog && content instanceof Component) {
            asWindow(targetWindow).getContentPane().add((Component) content);
        }
    }


    public void setWindowSize(Object targetWindow, int width, int height) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).setSize(width, height);
        }
    }


    public void addMenu(Object targetWindow, Object targetMenu) {
        if (targetWindow instanceof JDialog && targetMenu instanceof JMenu) {
            JDialog dialog = asWindow(targetWindow);
            JMenu menu = (JMenu) targetMenu;
            if (dialog.getJMenuBar() == null) {
                dialog.setJMenuBar(new JMenuBar());
            }
            dialog.getJMenuBar().add(menu);
        }
    }


    public void setWindowTitle(Object targetWindow, String title) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).setTitle(title);
        }
    }


    public void repaintWindow(Object targetWindow) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).repaint();
        }
    }


    public void repaintComponent(Object targetComponent) {
        if (targetComponent instanceof Component) {
            ((Component) targetComponent).repaint();
        }
    }


    public String getText(Object target) {
        Object tmp = target;
        if (tmp instanceof JScrollPane) {
            tmp = ((JScrollPane) tmp).getViewport().getView();
        }
        if (tmp instanceof JTextComponent) {
            return ((JTextComponent) tmp).getText();
        } else {
            return "";
        }
    }

    public String getSelectedText(Object target) {
        Object tmp = target;
        if (tmp instanceof JScrollPane) {
            tmp = ((JScrollPane) tmp).getViewport().getView();
        }
        if (tmp instanceof JTextComponent) {
            return ((JTextComponent) tmp).getSelectedText();
        } else {
            return "";
        }
    }


    public void setText(Object target, String text) {
        Object tmp = target;
        if (tmp instanceof JScrollPane) {
            tmp = ((JScrollPane) tmp).getViewport().getView();
        }
        if (tmp instanceof JTextComponent) {
            ((JTextComponent) tmp).setText(text);
        }
    }


    public void replaceSelectedText(Object target, String text) {
        Object tmp = target;
        if (tmp instanceof JScrollPane) {
            tmp = ((JScrollPane) tmp).getViewport().getView();
        }
        if (tmp instanceof JTextComponent) {
            ((JTextComponent) tmp).replaceSelection(text);
        }
    }
}
