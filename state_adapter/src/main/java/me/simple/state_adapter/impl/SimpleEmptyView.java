package me.simple.state_adapter.impl;

import android.view.View;

import me.simple.state_adapter.R;
import me.simple.state_adapter.StateViewHolder;
import me.simple.state_adapter.abs.StateEmptyView;

public class SimpleEmptyView extends StateEmptyView {

    @Override
    public int setLayoutRes() {
        return R.layout.simple_empty_view;
    }

    @Override
    public void onCreate(View view) {
        super.onCreate(view);
    }

    @Override
    public void onAttachedToWindow(StateViewHolder viewHolder) {
        super.onAttachedToWindow(viewHolder);
    }

    @Override
    public void onDetachedFromWindow(StateViewHolder viewHolder) {
        super.onDetachedFromWindow(viewHolder);
    }
}
