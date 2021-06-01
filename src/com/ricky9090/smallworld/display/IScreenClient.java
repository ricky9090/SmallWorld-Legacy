package com.ricky9090.smallworld.display;

import com.ricky9090.smallworld.view.advui.STView;

public interface IScreenClient {

    void onUpdate();

    void commit(STView target, int action);
}
