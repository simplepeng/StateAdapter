package me.simple.state_adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_STATE = 1;

    private RecyclerView.Adapter mRealAdapter;
    private AbsStateView mStateView;
    private StateViewHolder mStateViewHolder;

    public static final int STATE_LOADING = 0;
    public static final int STATE_EMPTY = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_RETRY = 3;
    public static final int STATE_CONTENT = 4;
    private int mCurrentState = STATE_LOADING;

    private OnRetryItemClickListener mOnRetryItemClickListener;

    private StateAdapter(RecyclerView.Adapter adapter) {
        this(adapter, new BaseStateView());
    }

    private StateAdapter(RecyclerView.Adapter adapter, AbsStateView stateView) {
        if (adapter == null) throw new NullPointerException("adapter can not be null");
        this.mRealAdapter = adapter;
        this.mStateView = stateView;
    }

    public static StateAdapter wrap(RecyclerView.Adapter adapter) {
        return new StateAdapter(adapter);
    }

    public static StateAdapter wrap(RecyclerView.Adapter adapter, AbsStateView stateView) {
        return new StateAdapter(adapter, stateView);
    }

    public StateAdapter setOnRetryItemClickListener(OnRetryItemClickListener listener) {
        this.mOnRetryItemClickListener = listener;
        return this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mStateView == null) {
            throw new NullPointerException("State View no implements");
        }

        if (viewType == VIEW_TYPE_STATE) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View stateView = inflater.inflate(mStateView.setLayoutRes(), viewGroup, false);
            mStateView.onCreate(stateView);
            this.mStateViewHolder = new StateViewHolder(stateView, mStateView);
            return mStateViewHolder;
        }

        return mRealAdapter.onCreateViewHolder(viewGroup, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_STATE) {
            final StateViewHolder holder = (StateViewHolder) viewHolder;
            holder.setState(mCurrentState);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRetryItemClickListener == null || mCurrentState != STATE_RETRY) return;
                    mOnRetryItemClickListener.onClick(holder, holder.getAdapterPosition());
                }
            });
        } else {
            mRealAdapter.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (mRealAdapter.getItemCount() == 0) {
            return 1;
        }
        return mRealAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mRealAdapter.getItemCount() == 0) return VIEW_TYPE_STATE;
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRealAdapter.registerAdapterDataObserver(mDataObserver);
        mRealAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mRealAdapter.unregisterAdapterDataObserver(mDataObserver);
        mRealAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            StateAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            StateAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            StateAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            StateAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            StateAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            StateAdapter.this.notifyItemRangeChanged(fromPosition, toPosition, itemCount);
        }
    };

    public void showLoading() {
        mCurrentState = STATE_LOADING;
        notifyStateVH();
    }

    public void showEmpty() {
        mCurrentState = STATE_EMPTY;
        notifyStateVH();
    }

    public void showError() {
        mCurrentState = STATE_ERROR;
        notifyStateVH();
    }

    public void showRetry() {
        mCurrentState = STATE_RETRY;
        notifyStateVH();
    }

    public void showContent() {
        mCurrentState = STATE_CONTENT;
        notifyDataSetChanged();
    }

    private void notifyStateVH() {
        notifyItemChanged(0);
    }


    public interface OnRetryItemClickListener {
        void onClick(StateViewHolder holder, int position);
    }
}
