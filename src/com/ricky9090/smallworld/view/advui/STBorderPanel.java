package com.ricky9090.smallworld.view.advui;

public class STBorderPanel extends STViewGroup {

    private STView mCenter;
    private STView mTop;
    private STView mRight;
    private STView mBottom;
    private STView mLeft;

    public STView getCenter() {
        return mCenter;
    }

    public void setCenter(STView center) {
        this.mCenter = center;
    }

    public STView getTop() {
        return mTop;
    }

    public void setTop(STView top) {
        this.mTop = top;
    }

    public STView getRight() {
        return mRight;
    }

    public void setRight(STView right) {
        this.mRight = right;
    }

    public STView getBottom() {
        return mBottom;
    }

    public void setBottom(STView bottom) {
        this.mBottom = bottom;
    }

    public STView getLeft() {
        return mLeft;
    }

    public void setLeft(STView left) {
        this.mLeft = left;
    }

    @Override
    public STView findViewById(int id) {
        if (this.getId() == id) {
            return this;
        }

        if (mCenter != null) {
            STView tmp = mCenter.findViewById(id);
            if (tmp != null) {
                return tmp;
            }
        }

        if (mTop != null) {
            STView tmp = mTop.findViewById(id);
            if (tmp != null) {
                return tmp;
            }
        }

        if (mRight != null) {
            STView tmp = mRight.findViewById(id);
            if (tmp != null) {
                return tmp;
            }
        }

        if (mBottom != null) {
            STView tmp = mBottom.findViewById(id);
            if (tmp != null) {
                return tmp;
            }
        }

        if (mLeft != null) {
            STView tmp = mLeft.findViewById(id);
            if (tmp != null) {
                return tmp;
            }
        }

        return null;
    }
}
