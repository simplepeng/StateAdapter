package me.simple.state_adapter;

import android.view.View;

public interface AbsStateView {

    int setLayoutRes();

    void onCreate(View stateView);

    void showLoading();

    void showEmpty();

    void showError();

    void showRetry();

}
