package com.ricky9090.smallworld.view.advui;

import java.util.ArrayList;
import java.util.List;

public class STMenu extends STTextView {

    private final List<STMenuItem> itemList = new ArrayList<>();

    public void addMenuItem(STMenuItem item) {
        itemList.add(item);
    }

    public List<STMenuItem> getItemList() {
        return itemList;
    }

}
