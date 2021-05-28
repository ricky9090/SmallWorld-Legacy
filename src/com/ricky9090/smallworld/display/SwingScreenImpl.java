package com.ricky9090.smallworld.display;

import com.ricky9090.smallworld.SmallInterpreter;
import com.ricky9090.smallworld.obj.SmallByteArray;
import com.ricky9090.smallworld.obj.SmallJavaObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    public Object createWindow() {
        JDialog jd = new JDialog();
        jd.setVisible(false);
        return jd;
    }

    @Override
    public Object createPanel(String content) {
        JLabel jl = new JLabel(content);
        return new JScrollPane(jl);
    }

    @Override
    public Object createButton(String text, final ButtonListener listener) {
        JButton jb = new JButton(text);
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listener.onClick();
            }
        });
        return jb;
    }

    @Override
    public Object createTextLine() {
        return new JTextField();
    }

    @Override
    public Object createTextArea() {
        JTextArea jta = new JTextArea();
        jta.setTabSize(4);
        return new JScrollPane(jta);
    }

    @Override
    public Object createGridPanel(Object[] dataArray, int rows, int cols) {
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < dataArray.length; i++) {
            jp.add((Component) ((SmallJavaObject) dataArray[i]).value);
        }
        return jp;
    }

    @Override
    public Object createListPanel(Object[] dataArray, ListListener listener) {
        final JList<Object> jList = new JList<>(dataArray);
        jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if ((!e.getValueIsAdjusting()) && (jList.getSelectedIndex() >= 0)) {
                    listener.onItemClick(jList.getSelectedIndex());
                }
            }
        });
        return new JScrollPane(jList);
    }

    @Override
    public Object createMenu(String title) {
        return new JMenu(title);
    }

    @Override
    public void addMenuItem(Object targetMenu, String text, ButtonListener listener) {
        if (targetMenu instanceof JMenu) {
            JMenu menu = (JMenu) targetMenu;
            JMenuItem ji = new JMenuItem(text);
            ji.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.onClick();
                }
            });
            menu.add(ji);
        }
    }

    @Override
    public Object createBorderPanel() {
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        return jp;
    }

    @Override
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

    @Override
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

    @Override
    public void showWindow(Object targetWindow) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).setVisible(true);
        }
    }

    @Override
    public void hideWindow(Object targetWindow) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).setVisible(false);
        }
    }

    @Override
    public void setWindowContent(Object targetWindow, Object content) {
        if (targetWindow instanceof JDialog && content instanceof Component) {
            asWindow(targetWindow).getContentPane().add((Component) content);
        }
    }

    @Override
    public void setWindowSize(Object targetWindow, int width, int height) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).setSize(width, height);
        }
    }

    @Override
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

    @Override
    public void setWindowTitle(Object targetWindow, String title) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).setTitle(title);
        }
    }

    @Override
    public void repaintWindow(Object targetWindow) {
        if (targetWindow instanceof JDialog) {
            asWindow(targetWindow).repaint();
        }
    }

    @Override
    public void repaintComponent(Object targetComponent) {
        if (targetComponent instanceof Component) {
            ((Component) targetComponent).repaint();
        }
    }

    @Override
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

    @Override
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

    @Override
    public void setText(Object target, String text) {
        Object tmp = target;
        if (tmp instanceof JScrollPane) {
            tmp = ((JScrollPane) tmp).getViewport().getView();
        }
        if (tmp instanceof JTextComponent) {
            ((JTextComponent) tmp).setText(text);
        }
    }

    @Override
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
