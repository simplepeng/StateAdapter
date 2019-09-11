package me.simple.demo;

import android.view.View;

import me.simple.state_adapter.IStateView;

public class CustomStateView implements IStateView {
    @Override
    public int setLayoutRes() {
        return 0;
    }

    @Override
    public void onCreate(View stateView) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showRetry() {

    }
}
