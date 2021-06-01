package com.ricky9090.smallworld.policy;

import com.ricky9090.smallworld.display.IScreenService;
import com.ricky9090.smallworld.display.service.ScreenServiceImpl;

public class ScreenPolicy {

    public static IScreenService provideScreen() {
        return new ScreenServiceImpl();
    }
}
