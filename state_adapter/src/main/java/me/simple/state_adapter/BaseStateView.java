package me.simple.state_adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class BaseStateView implements IStateView {

    private ProgressBar mProgressBar;
    private ImageView mEmpty;
    private ImageView mError;
    private ImageView mRetry;

    @Override
    public int setLayoutRes() {
        return R.layout.base_state_view;
    }

    @Override
    public void onCreate(View stateView) {
        mProgressBar = stateView.findViewById(R.id.sa_progressBar);
        mEmpty = stateView.findViewById(R.id.sa_empty);
        mError = stateView.findViewById(R.id.sa_error);
        mRetry = stateView.findViewById(R.id.sa_retry);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mEmpty.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mRetry.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty() {
        mProgressBar.setVisibility(View.GONE);
        mEmpty.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);
        mRetry.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        mProgressBar.setVisibility(View.GONE);
        mEmpty.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
        mRetry.setVisibility(View.GONE);
    }

    @Override
    public void showRetry() {
        mProgressBar.setVisibility(View.GONE);
        mEmpty.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mRetry.setVisibility(View.VISIBLE);
    }
}
