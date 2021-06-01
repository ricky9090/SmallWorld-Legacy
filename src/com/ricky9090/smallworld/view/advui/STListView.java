package com.ricky9090.smallworld.view.advui;

import com.ricky9090.smallworld.obj.SmallObject;

import java.util.ArrayList;
import java.util.List;

public class STListView extends STViewGroup {

    private final List<SmallObject> dataList = new ArrayList<>();

    private int mSelectedIndex;
    private ListListener mListener;
    private final ListListener foobarListener = new ListListener() {
        @Override
        public void onListItemClick(int zeroBaseIndex, SmallObject data) {

        }
    };

    public void setDataList(List<SmallObject> list) {
        if (list == null) {
            return;
        }
        dataList.clear();
        dataList.addAll(list);
    }

    public void clearDataList() {
        dataList.clear();
    }

    public void addDataList(List<SmallObject> list) {
        if (list == null) {
            return;
        }
        dataList.addAll(list);
    }

    public void addData(SmallObject data) {
        if (data == null) {
            return;
        }
        dataList.add(data);
    }

    public List<SmallObject> getDataList() {
        return dataList;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.mSelectedIndex = selectedIndex;
    }

    public void setListListener(ListListener listener) {
        this.mListener = listener;
    }

    public ListListener getListener() {
        if (mListener == null) {
            return foobarListener;
        }
        return mListener;
    }

    public interface ListListener {
        void onListItemClick(int zeroBaseIndex, SmallObject data);
    }
}
