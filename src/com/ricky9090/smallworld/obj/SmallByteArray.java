package com.ricky9090.smallworld.obj;

public class SmallByteArray extends SmallObject {

    public byte[] values;

    public SmallByteArray(SmallObject cl, int size) {
        super(cl, 0);
        values = new byte[size];
    }

    public SmallByteArray(SmallObject cl, String text) {
        super(cl, 0);
        values = text.getBytes();
    }

    public SmallByteArray() {
        super();
    }

    public String toString() {
        // we assume its a string, tho not always true...
        return new String(values);
    }

    public SmallObject copy(SmallObject cl) {
        SmallByteArray newObj = new SmallByteArray(cl, values.length);
        for (int i = 0; i < values.length; i++) {
            newObj.values[i] = values[i];
        }
        return newObj;
    }
}