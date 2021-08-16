package me.simple.state_adapter.impl;

import me.simple.state_adapter.R;
import me.simple.state_adapter.abs.StateErrorView;

public class SimpleErrorView extends StateErrorView {
    @Override
    public int setLayoutRes() {
        return R.layout.adapter_error_view;
    }
}
