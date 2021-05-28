package com.ricky9090.smallworld.policy;

import com.ricky9090.smallworld.display.IScreen;
import com.ricky9090.smallworld.display.SwingScreenImpl;

public class ScreenPolicy {

    public static IScreen provideScreen() {
        return new SwingScreenImpl();
    }
}
