package com.ricky9090.smallworld;

import com.ricky9090.smallworld.obj.SmallObject;

public class Sema {

    private SmallObject value;
    private boolean hasBeenSet = false;

    public synchronized SmallObject get() {
        if (!hasBeenSet) {
            try {
                wait();
            } catch (Exception e) {
                System.out.println("Sema got exception " + e);
            }
        }
        return value;
    }

    public synchronized void set(SmallObject v) {
        value = v;
        hasBeenSet = true;
        notifyAll();
    }

}
