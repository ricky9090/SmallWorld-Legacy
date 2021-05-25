package com.ricky9090.smallworld;

import com.ricky9090.smallworld.obj.SmallObject;

public class SmallException extends Exception {

    public SmallObject context;

    SmallException(String gripe, SmallObject c) {
        super(gripe);
        context = c;
    }

}