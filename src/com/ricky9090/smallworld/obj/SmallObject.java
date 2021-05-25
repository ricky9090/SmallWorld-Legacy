package com.ricky9090.smallworld.obj;

public class SmallObject {

    public int id; // used to speed up image save, usually null

    public SmallObject objClass;
    public SmallObject[] data;

    public SmallObject() {
        objClass = null;
        data = null;
    }

    public SmallObject(SmallObject cl, int size) {
        objClass = cl;
        data = new SmallObject[size];
    }

    public SmallObject copy(SmallObject cl) {
        return this;
    }

    public SmallObject duplicate() {
        SmallObject d = new SmallObject();
        d.objClass = objClass;
        d.data = data;
        return d;
    }

}