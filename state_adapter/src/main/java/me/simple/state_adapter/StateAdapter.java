package me.simple.state_adapter;

import android.database.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.simple.state_adapter.abs.StateEmptyView;
import me.simple.state_adapter.abs.StateErrorView;
import me.simple.state_adapter.abs.StateLoadingView;
import me.simple.state_adapter.abs.StateRetryView;
import me.simple.state_adapter.abs.StateView;

@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
public class StateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    private final int VIEW_TYPE_STATE = 1111;

    private RecyclerView.Adapter mRealAdapter;
//    private StateView mStateView;

    public static final int TYPE_STATE_NORMAL = -111;
    public static final int TYPE_STATE_LOADING = 111;
    public static final int TYPE_STATE_EMPTY = 222;
    public static final int TYPE_STATE_ERROR = 333;
    public static final int TYPE_STATE_RETRY = 444;
    public static final int TYPE_STATE_CONTENT = 555;
    private int mTypeState = TYPE_STATE_NORMAL;

    //    private HashMap<Integer, StateView> mStateViewMap = new HashMap<>();
    private SparseArray<StateView> mStateViewMap = new SparseArray<>();

    private HashMap<Integer, View.OnClickListener> mViewClicks = new HashMap<>();

    private StateAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) throw new NullPointerException("adapter can not be null");
        this.mRealAdapter = adapter;
    }

    public static StateAdapter wrap(RecyclerView.Adapter adapter) {
        return new StateAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        if (isTypeState()) {
            return 1;
        }
        return mRealAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
//        LogHelper.d("getItemViewType");
        if (position == 0 && isTypeState()) return mTypeState;
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
        LogHelper.d("onCreateViewHolder");

        if (isTypeState()) {
            StateView stateView = getStateView(mTypeState);
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View stateItemView = inflater.inflate(stateView.setLayoutRes(), viewGroup, false);

            StateViewHolder stateViewHolder = new StateViewHolder(stateItemView);
            stateView.onCreate(stateItemView);

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
        LogHelper.d("onBindViewHolder => " + viewHolder.getClass().getName());
        if (viewHolder instanceof StateViewHolder) {
            final StateViewHolder holder = (StateViewHolder) viewHolder;
            holder.setState(mTypeState);
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
//        LogHelper.d("onViewAttachedToWindow => "+holder.getClass().getName());
        if (holder instanceof StateViewHolder) {
            return;
        }
        mRealAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
//        LogHelper.d("onViewDetachedFromWindow => "+holder.getClass().getName());
        if (holder instanceof StateViewHolder) {
            return;
        }
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

    /**
     *
     */
    private void setFullSpan(RecyclerView recyclerView) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gm = (GridLayoutManager) layoutManager;
            gm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (isTypeState()) return gm.getSpanCount();
                    return 1;
                }
            });
        }
    }

    /**
     *
     */
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
            mTypeState = TYPE_STATE_CONTENT;
            StateAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mTypeState = TYPE_STATE_CONTENT;
            StateAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mTypeState = TYPE_STATE_CONTENT;
            StateAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mTypeState = TYPE_STATE_CONTENT;
            StateAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mTypeState = TYPE_STATE_CONTENT;
            StateAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mTypeState = TYPE_STATE_CONTENT;
            StateAdapter.this.notifyItemRangeChanged(fromPosition, toPosition, itemCount);
        }
    };

    public StateAdapter register(StateView stateView) {
        if (stateView instanceof StateEmptyView) {
            mStateViewMap.put(TYPE_STATE_EMPTY, stateView);
        } else if (stateView instanceof StateLoadingView) {
            mStateViewMap.put(TYPE_STATE_LOADING, stateView);
        } else if (stateView instanceof StateErrorView) {
            mStateViewMap.put(TYPE_STATE_ERROR, stateView);
        } else if (stateView instanceof StateRetryView) {
            mStateViewMap.put(TYPE_STATE_RETRY, stateView);
        }
        return this;
    }

    private StateView getStateView(int type) {
        StateView stateView = mStateViewMap.get(type);
        if (stateView == null) {
            throw new NullPointerException("do you have register this type? type is" + getTypeName(mTypeState));
        }
        return stateView;
    }

    private String getTypeName(int type) {
        String typeName = "";
        switch (type) {
            case TYPE_STATE_EMPTY:
                typeName = "EMPTY";
                break;
            case TYPE_STATE_LOADING:
                typeName = "LOADING";
                break;
            case TYPE_STATE_ERROR:
                typeName = "ERROR";
                break;
            case TYPE_STATE_RETRY:
                typeName = "RETRY";
                break;
        }
        return typeName;
    }

    public void showLoading() {
        mTypeState = TYPE_STATE_LOADING;
        notifyStateVH();
    }

    public void showEmpty() {
        mTypeState = TYPE_STATE_EMPTY;
        notifyStateVH();
    }

    public void showError() {
        mTypeState = TYPE_STATE_ERROR;
        notifyStateVH();
    }

    public void showRetry() {
        mTypeState = TYPE_STATE_RETRY;
        notifyStateVH();
    }

    public void showContent() {
        mTypeState = TYPE_STATE_CONTENT;
        notifyDataSetChanged();
    }

    private void notifyStateVH() {
        StateAdapter.this.notifyDataSetChanged();
    }

    private boolean isTypeState() {
        return mTypeState == TYPE_STATE_LOADING
                || mTypeState == TYPE_STATE_EMPTY
                || mTypeState == TYPE_STATE_ERROR
                || mTypeState == TYPE_STATE_RETRY;
    }

    private void setClick(final View itemView, final StateViewHolder stateViewHolder) {
        for (Map.Entry<Integer, View.OnClickListener> entry : mViewClicks.entrySet()) {
            Integer viewId = entry.getKey();
            View.OnClickListener listener = entry.getValue();
            View child = itemView.findViewById(viewId);
            if (child != null) {
                child.setOnClickListener(listener);
            }
        }
    }

    public StateAdapter setOnItemViewClickListener(int viewId, View.OnClickListener listener) {
        mViewClicks.put(viewId, listener);
        return this;
    }
}
