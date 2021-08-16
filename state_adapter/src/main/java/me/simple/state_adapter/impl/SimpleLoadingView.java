package me.simple.state_adapter.impl;

import me.simple.state_adapter.R;
import me.simple.state_adapter.abs.StateLoadingView;

public class SimpleLoadingView extends StateLoadingView {

    @Override
    public int setLayoutRes() {
        return R.layout.adapter_loading_view;
    }
}
