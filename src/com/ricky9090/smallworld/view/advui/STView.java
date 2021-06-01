package com.ricky9090.smallworld.view.advui;

public class STView {

    public static final int EVENT_DOWN = 0;
    public static final int EVENT_MOVE = 1;
    public static final int EVENT_UP = 2;

    public static final int ST_VIEW_INVISIBLE = 0;
    public static final int ST_VIEW_VISIBLE = 1;

    private final int id = this.hashCode();
    //private int version = 0;  // start from 0

    private int mWidth = -1;
    private int mHeight = -1;

    private InputEventListener mInputListener;

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getId() {
        return id;
    }

    public STView findViewById(int id) {
        if (this.id == id) {
            return this;
        }
        return null;
    }

    public void addInputEventListener(InputEventListener listener) {
        mInputListener = listener;
    }

    public interface InputEventListener {
        void onInputEvent(int eventType, int x, int y);
    }
}
