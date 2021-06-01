package com.ricky9090.smallworld.obj;

import com.ricky9090.smallworld.view.advui.STView;

public class SmallJavaObject extends SmallObject {

    public Object value;

    public SmallJavaObject(SmallObject cls, Object v) {
        super(cls, 0);
        value = v;
    }

    public SmallJavaObject() {
        super();
    }

    public STView valueAsView() {
        return (STView) value;
    }

    /*public STView valueAsView() {
        return (STView) value;
    }

    public STWindow valueAsWindow() {
        return (STWindow) value;
    }

    public STMenu valueAsMenu() {
        return (STMenu) value;
    }

    public STPanel valueAsPanel() {
        return (STPanel) value;
    }

    public STImageView valueAsImageView() {
        return (STImageView) value;
    }

    public STTextView valueAsTextView() {
        return (STTextView) value;
    }

    public STListView valueAsListView() {
        return (STListView) value;
    }*/

}