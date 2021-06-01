package com.ricky9090.smallworld.view.advui;

import java.util.ArrayList;
import java.util.List;

public class STViewGroup extends STView {

    private final List<STView> mChildren = new ArrayList<>();

    public List<STView> getChildren() {
        return mChildren;
    }

    public void addChild(STView view) {
        mChildren.add(view);
    }

    @Override
    public STView findViewById(int id) {
        if (this.getId() == id) {
            return this;
        }

        for (STView view : mChildren) {
            if (view.getId() == id) {
                return view;
            }
        }

        return null;
    }
}
