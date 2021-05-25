package com.ricky9090.smallworld.obj;

public class SmallJavaObject extends SmallObject {

    public Object value;

    public SmallJavaObject(SmallObject cls, Object v) {
        super(cls, 0);
        value = v;
    }

    public SmallJavaObject() {
        super();
    }

}