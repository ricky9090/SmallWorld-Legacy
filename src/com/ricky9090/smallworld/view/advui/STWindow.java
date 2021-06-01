package com.ricky9090.smallworld.view.advui;

import java.util.ArrayList;
import java.util.List;

public class STWindow extends STViewGroup {

    private String mTitle;
    private final List<STMenu> mMenuList = new ArrayList<>();
    private STView mContent;

    private boolean isVisible;

    private WindowListener mListener;
    private final WindowListener foobarListener = new WindowListener() {
        @Override
        public void onWindowClosing() {

        }
    };

    public STWindow() {
        isVisible = false;
    }

    public void setContent(STView content) {
        this.mContent = content;
    }

    public STView getContent() {
        return mContent;
    }

    public void addMenu(STMenu targetMenu) {
        mMenuList.add(targetMenu);
    }


    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public List<STMenu> getMenuList() {
        return mMenuList;
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public WindowListener getListener() {
        if (mListener == null) {
            return foobarListener;
        }
        return mListener;
    }

    @Override
    public STView findViewById(int id) {
        if (mContent == null) {
            return null;
        }

        return mContent.findViewById(id);
    }

    public interface WindowListener {
        void onWindowClosing();
    }

    public void setWindowListener(WindowListener listener) {
        mListener = listener;
    }
}
