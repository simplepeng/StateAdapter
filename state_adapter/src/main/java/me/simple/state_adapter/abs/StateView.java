package me.simple.state_adapter.abs;

import android.view.View;

import me.simple.state_adapter.StateViewHolder;

public abstract class StateView {

    public abstract int setLayoutRes();

    public void onCreate(View view) {

    }

    public void onAttachedToWindow(StateViewHolder viewHolder) {

    }

    public void onDetachedFromWindow(StateViewHolder viewHolder) {

    }
}
