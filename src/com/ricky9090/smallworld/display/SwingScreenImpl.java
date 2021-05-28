package com.ricky9090.smallworld.display;

import javax.swing.*;
import java.awt.*;

public class SwingScreenImpl implements IScreen {

    public SwingScreenImpl() {
    }

    private JDialog asWindow(Object target) {
        return (JDialog) target;
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
}
