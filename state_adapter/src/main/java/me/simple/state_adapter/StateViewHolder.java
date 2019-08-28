package me.simple.state_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

public class StateViewHolder extends RecyclerView.ViewHolder {

    private IStateView mStateView;

    public StateViewHolder(@NonNull View itemView, IStateView stateView) {
        super(itemView);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
        }
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
