package me.simple.state_adapter.impl;

import me.simple.state_adapter.R;
import me.simple.state_adapter.abs.StateRetryView;

public class SimpleRetryView extends StateRetryView {
    @Override
    public int setLayoutRes() {
        return R.layout.simple_retry_view;
    }
}
