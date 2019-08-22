package me.simple.state_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class StateViewHolder extends RecyclerView.ViewHolder {

    private AbsStateView mStateView;

    public StateViewHolder(@NonNull View itemView, AbsStateView stateView) {
        super(itemView);
        this.mStateView = stateView;
    }

    public void setState(int state) {
        switch (state) {
            case StateAdapter.STATE_LOADING:
                mStateView.showLoading();
                break;
            case StateAdapter.STATE_EMPTY:
                mStateView.showEmpty();
                break;
            case StateAdapter.STATE_ERROR:
                mStateView.showError();
                break;
            case StateAdapter.STATE_RETRY:
                mStateView.showRetry();
                break;
        }
    }
}
