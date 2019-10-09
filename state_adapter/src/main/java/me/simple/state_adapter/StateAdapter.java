package me.simple.state_adapter;

import android.database.Observable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
public class StateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_STATE = 1111;

    private RecyclerView.Adapter mRealAdapter;
    private IStateView mStateView;

    public static final int STATE_NORMAL = -1;
    public static final int STATE_LOADING = 0;
    public static final int STATE_EMPTY = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_RETRY = 3;
    public static final int STATE_CONTENT = 4;
    private int mCurrentState = STATE_NORMAL;

    private HashMap<Integer, View.OnClickListener> mViewClicks = new HashMap<>();

    private StateAdapter(RecyclerView.Adapter adapter) {
        this(adapter, new BaseStateView());
    }

    private StateAdapter(RecyclerView.Adapter adapter, IStateView stateView) {
        if (adapter == null) throw new NullPointerException("adapter can not be null");
        this.mRealAdapter = adapter;
        this.mStateView = stateView;
    }

    public static StateAdapter wrap(RecyclerView.Adapter adapter) {
        return new StateAdapter(adapter);
    }

    public static StateAdapter wrap(RecyclerView.Adapter adapter, IStateView stateView) {
        return new StateAdapter(adapter, stateView);
    }

    @Override
    public int getItemCount() {
        LogHelper.d("getItemCount state == " + mCurrentState);
        if (isTypeState()) {
            return 1;
        }
        return mRealAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isTypeState()) return VIEW_TYPE_STATE;
        return mRealAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        if (position == 0 && isTypeState()) return super.getItemId(position);
        return mRealAdapter.getItemId(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mStateView == null) {
            throw new NullPointerException("State View no implements");
        }

        if (viewType == VIEW_TYPE_STATE) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View stateItemView = inflater.inflate(mStateView.setLayoutRes(), viewGroup, false);
            StateViewHolder stateViewHolder = new StateViewHolder(stateItemView, mStateView);
            mStateView.onCreate(stateItemView);
            setClick(stateItemView, stateViewHolder);
            return stateViewHolder;
        }

        return mRealAdapter.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        onBindViewHolder(viewHolder, position, Collections.emptyList());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, List<Object> payloads) {
        if (viewHolder instanceof StateViewHolder) {
            final StateViewHolder holder = (StateViewHolder) viewHolder;
            holder.setState(mCurrentState);
        } else {
            mRealAdapter.onBindViewHolder(viewHolder, position, payloads);
        }
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        if (holder instanceof StateViewHolder) return false;
        return mRealAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof StateViewHolder) return;
        mRealAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof StateViewHolder) return;
        mRealAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof StateViewHolder) return;
        mRealAdapter.onViewRecycled(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        setFullSpan(recyclerView);

        if (!isRegistered()) {
            mRealAdapter.registerAdapterDataObserver(mDataObserver);
        }

        mRealAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        if (isRegistered()) {
            mRealAdapter.unregisterAdapterDataObserver(mDataObserver);
        }

        mRealAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    private void setFullSpan(RecyclerView recyclerView) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gm = (GridLayoutManager) layoutManager;
            gm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (viewType == VIEW_TYPE_STATE) return gm.getSpanCount();
                    return 1;
                }
            });
        }
    }

    private boolean isRegistered() {
        boolean isRegistered = false;
        try {
            Class<? extends RecyclerView.Adapter> clazz = RecyclerView.Adapter.class;
            Field field = clazz.getDeclaredField("mObservable");
            field.setAccessible(true);
            Observable observable = (Observable) field.get(mRealAdapter);

            Field observersField = Observable.class.getDeclaredField("mObservers");
            observersField.setAccessible(true);
            ArrayList<Object> list = (ArrayList<Object>) observersField.get(observable);
            isRegistered = list.contains(mDataObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRegistered;
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mCurrentState = STATE_CONTENT;
            StateAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mCurrentState = STATE_CONTENT;
            StateAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mCurrentState = STATE_CONTENT;
            StateAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mCurrentState = STATE_CONTENT;
            StateAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mCurrentState = STATE_CONTENT;
            StateAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mCurrentState = STATE_CONTENT;
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
        StateAdapter.this.notifyDataSetChanged();
    }

    private boolean isTypeState() {
        return mCurrentState == STATE_LOADING
                || mCurrentState == STATE_EMPTY
                || mCurrentState == STATE_ERROR
                || mCurrentState == STATE_RETRY;
    }

    private void setClick(final View itemView, final StateViewHolder stateViewHolder) {
        for (Map.Entry<Integer, View.OnClickListener> entry : mViewClicks.entrySet()) {
            Integer viewId = entry.getKey();
            View.OnClickListener listener = entry.getValue();
            View child = itemView.findViewById(viewId);
            child.setOnClickListener(listener);
        }
    }

    public StateAdapter setOnItemViewClickListener(int viewId, View.OnClickListener listener) {
        mViewClicks.put(viewId, listener);
        return this;
    }
}
