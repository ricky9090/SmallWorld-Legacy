package com.ricky9090.smallworld.obj;

public class SmallInt extends SmallObject {

    public int value;

    public SmallInt(SmallObject IntegerClass, int v) {
        super(IntegerClass, 0);
        value = v;
    }

    public SmallInt() {
        super();
    }

    public String toString() {
        return "SmallInt: " + value;
    }

    public Integer toInteger() {
        return Integer.valueOf(value);
    }
}