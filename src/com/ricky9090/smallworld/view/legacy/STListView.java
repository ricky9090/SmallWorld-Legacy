package com.ricky9090.smallworld.view.legacy;

import com.ricky9090.smallworld.obj.SmallObject;

public interface STListView extends STView {

    void setData(SmallObject[] dataList);

    void setListListener(ListListener listener);

    int getSelectedIndex();

    interface ListListener {
        void onItemClick(int zeroBaseIndex);
    }
}
