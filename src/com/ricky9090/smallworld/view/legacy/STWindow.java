package com.ricky9090.smallworld.view.legacy;

public interface STWindow extends STView {

    void show();

    void hide();

    void setContent(STView content);

    void setSize(int width, int height);

    void addMenu(STMenu targetMenu);

    void setTitle(String title);

    void setWindowListener(WindowListener listener);

    interface WindowListener {
        void onWindowClose();
    }
}
