package me.simple.state_adapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("NotifyDataSetChanged")
class StateAdapter<VH : ViewHolder> private constructor(
    private val builder: Builder,
    val bindAdapter: RecyclerView.Adapter<VH>
) : RecyclerView.Adapter<ViewHolder>() {

    //当前的状态
    private var mTypeState = TYPE_STATE_NORMAL

    //点击事件
    private val mViewClicks = SparseArray<View.OnClickListener>()

    override fun getItemCount(): Int {
        return if (isTypeState)
            1
        else
            bindAdapter.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && isTypeState)
            mTypeState.hashCode()
        else
            bindAdapter.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0 && isTypeState)
            super.getItemId(position)
        else
            bindAdapter.getItemId(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        //如果是加载状态布局
        if (isTypeState) {
            val stateView = getStateView(mTypeState)!!
            val inflater = LayoutInflater.from(viewGroup.context)
            val stateItemView = inflater.inflate(stateView.setLayoutRes(), viewGroup, false)
            val stateViewHolder = StateViewHolder(stateItemView)
            stateView.onCreate(stateItemView)
            setClick(stateItemView, stateViewHolder)
            return stateViewHolder
        }
        //
        return bindAdapter.onCreateViewHolder(viewGroup, viewType)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        onBindViewHolder(viewHolder, position, emptyList())
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, payloads: List<Any>) {
        //如果不是加载状态的ViewHolder
        if (viewHolder !is StateViewHolder) {
            bindAdapter.onBindViewHolder(asVH(viewHolder), position, payloads)
        }
    }

    override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
        return if (holder is StateViewHolder)
            false
        else
            bindAdapter.onFailedToRecycleView(asVH(holder))
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        if (holder is StateViewHolder) {
            getStateView(mTypeState)?.onAttachedToWindow(holder)
            return
        }
        bindAdapter.onViewAttachedToWindow(asVH(holder))
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        if (holder is StateViewHolder) {
            getStateView(mTypeState)?.onDetachedFromWindow(holder)
            return
        }
        bindAdapter.onViewDetachedFromWindow(asVH(holder))
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if (holder is StateViewHolder) return
        bindAdapter.onViewRecycled(asVH(holder))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        setFullSpan(recyclerView)
        if (!isRegistered.get()) {
            bindAdapter.registerAdapterDataObserver(mDataObserver)
        }
        bindAdapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (isRegistered.get()) {
            bindAdapter.unregisterAdapterDataObserver(mDataObserver)
        }
        bindAdapter.onDetachedFromRecyclerView(recyclerView)
    }

    @Suppress("UNCHECKED_CAST")
    private fun asVH(holder: ViewHolder) = holder as VH

    /**
     * 设置能占满一屏
     */
    private fun setFullSpan(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager ?: return
        if (layoutManager is GridLayoutManager) {
            val gm = layoutManager
            gm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = getItemViewType(position)
                    return if (isTypeState) gm.spanCount else 1
                }
            }
        }
    }

    /**
     * 是否注册过DataObserver
     */
    private var isRegistered = AtomicBoolean(false)

//    private val isRegistered: Boolean
//        private get() {
//            var isRegistered = false
//            try {
//                val clazz: Class<out RecyclerView.Adapter<*>> = RecyclerView.Adapter::class.java
//                val field = clazz.getDeclaredField("mObservable")
//                field.isAccessible = true
//                val observable = field[bindAdapter] as Observable<*>
//                val observersField = Observable::class.java.getDeclaredField("mObservers")
//                observersField.isAccessible = true
//                val list = observersField[observable] as ArrayList<Any>
//                isRegistered = list.contains(mDataObserver)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return isRegistered
//        }

    private val mDataObserver: AdapterDataObserver = object : AdapterDataObserver() {

        override fun onChanged() {
            mTypeState = TYPE_STATE_CONTENT
            this@StateAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mTypeState = TYPE_STATE_CONTENT
            this@StateAdapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mTypeState = TYPE_STATE_CONTENT
            this@StateAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mTypeState = TYPE_STATE_CONTENT
            this@StateAdapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mTypeState = TYPE_STATE_CONTENT
            this@StateAdapter.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mTypeState = TYPE_STATE_CONTENT
            this@StateAdapter.notifyItemRangeChanged(fromPosition, toPosition, itemCount)
        }
    }

    /**
     * 获取状态布局
     */
    private fun getStateView(type: String): AdapterStateView? {
        return builder.stateMap[type]
    }

    fun showLoading() {
        mTypeState = TYPE_STATE_LOADING
        notifyStateVH()
    }

    fun showEmpty() {
        mTypeState = TYPE_STATE_EMPTY
        notifyStateVH()
    }

    fun showError() {
        mTypeState = TYPE_STATE_ERROR
        notifyStateVH()
    }

    fun showRetry() {
        mTypeState = TYPE_STATE_RETRY
        notifyStateVH()
    }

    fun showContent() {
        mTypeState = TYPE_STATE_CONTENT
        notifyDataSetChanged()
    }

    fun showCustom(key: String) {
        mTypeState = key
        notifyStateVH()
    }

    private fun notifyStateVH() {
        notifyDataSetChanged()
    }

    /**
     * 是否是状态布局
     */
    private val isTypeState: Boolean
        get() = builder.stateMap.containsKey(mTypeState)

    /**
     *
     */
    private fun setClick(itemView: View, holder: StateViewHolder) {
        for (i in 0 until mViewClicks.size()) {
            val viewId = mViewClicks.keyAt(i)
            val clickListener = mViewClicks.valueAt(i)
            val child = itemView.findViewById<View>(viewId)
            child?.setOnClickListener(clickListener)
        }
    }

    /**
     * 设置点击事件
     */
    fun setOnItemViewClickListener(viewId: Int, listener: View.OnClickListener) {
        mViewClicks.put(viewId, listener)
    }

    companion object {

        //状态预值
        const val TYPE_STATE_NORMAL = "TYPE_STATE_NORMAL"
        const val TYPE_STATE_LOADING = "TYPE_STATE_LOADING"
        const val TYPE_STATE_EMPTY = "TYPE_STATE_EMPTY"
        const val TYPE_STATE_ERROR = "TYPE_STATE_ERROR"
        const val TYPE_STATE_RETRY = "TYPE_STATE_RETRY"
        const val TYPE_STATE_CONTENT = "TYPE_STATE_CONTENT"

        @JvmStatic
        fun newBuilder() = Builder()

    }

    class Builder {

        //状态
        val stateMap = hashMapOf<String, AdapterStateView>()

        /**
         * 直接注册layoutId
         */
        fun registerLoading(layoutId: Int): Builder {
            stateMap[TYPE_STATE_LOADING] = StateViewWrapper(layoutId)
            return this
        }


        fun registerEmpty(layoutId: Int): Builder {
            stateMap[TYPE_STATE_EMPTY] = StateViewWrapper(layoutId)
            return this
        }


        fun registerError(layoutId: Int): Builder {
            stateMap[TYPE_STATE_ERROR] = StateViewWrapper(layoutId)
            return this
        }


        fun registerRetry(layoutId: Int): Builder {
            stateMap[TYPE_STATE_RETRY] = StateViewWrapper(layoutId)
            return this
        }

        fun registerCustom(key: String, layoutId: Int): Builder {
            if (stateMap.containsKey(key)) {
                throw IllegalArgumentException("don't use $key")
            }
            stateMap[key] = StateViewWrapper(layoutId)
            return this
        }

        /**
         * 注册AdapterStateView，可以回调onAttachedToWindow，onDetachedFromWindow函数
         */
        fun registerLoading(stateView: AdapterStateView): Builder {
            stateMap[TYPE_STATE_LOADING] = stateView
            return this
        }


        fun registerEmpty(stateView: AdapterStateView): Builder {
            stateMap[TYPE_STATE_EMPTY] = stateView
            return this
        }


        fun registerError(stateView: AdapterStateView): Builder {
            stateMap[TYPE_STATE_ERROR] = stateView
            return this
        }


        fun registerRetry(stateView: AdapterStateView): Builder {
            stateMap[TYPE_STATE_RETRY] = stateView
            return this
        }

        fun registerCustom(key: String, stateView: AdapterStateView): Builder {
            if (stateMap.containsKey(key)) {
                throw IllegalArgumentException("don't use $key")
            }
            stateMap[key] = stateView
            return this
        }

        /**
         *
         */
        fun <VH : ViewHolder> wrap(adapter: RecyclerView.Adapter<VH>?): StateAdapter<VH> {
            if (adapter == null) throw NullPointerException("adapter can not be null")
            return StateAdapter(this, adapter)
        }
    }
}