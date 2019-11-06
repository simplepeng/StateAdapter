package me.simple.state_adapter.impl;

import me.simple.state_adapter.R;
import me.simple.state_adapter.abs.StateEmptyView;

public class SimpleEmptyView extends StateEmptyView {

    @Override
    public int setLayoutRes() {
        return R.layout.simple_empty_view;
    }

}
