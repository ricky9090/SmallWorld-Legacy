package com.ricky9090.smallworld.view.impl.swing;

import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.STListView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SwingListView extends SwingBaseView implements STListView {

    private final JScrollPane mListPane;
    JList<SmallObject> mList;
    ListListener mListener;

    public SwingListView() {
        mList = new JList<>();
        mList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        mListPane = new JScrollPane(mList);

    }

    public SwingListView(SmallObject[] data) {
        mList = new JList<>(data);
        mList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        mListPane = new JScrollPane(mList);
    }

    @Override
    public int getSelectedIndex() {
        return mList.getSelectedIndex();
    }

    @Override
    public void setData(SmallObject[] dataList) {
        mList.setListData(dataList);
        repaint();
    }

    @Override
    public void setListListener(ListListener listener) {
        mList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if ((!e.getValueIsAdjusting()) && (mList.getSelectedIndex() >= 0)) {
                    listener.onItemClick(mList.getSelectedIndex());
                }
            }
        });
    }

    @Override
    public Object getRealComponent() {
        return mListPane;
    }

    @Override
    public void repaint() {
        mList.repaint();
    }
}
