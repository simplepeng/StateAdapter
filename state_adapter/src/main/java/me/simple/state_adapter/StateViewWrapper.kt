package me.simple.state_adapter

import android.view.View

internal class StateViewWrapper(private val layoutId: Int) : AdapterStateView {

    override fun setLayoutRes() = layoutId

    override fun onCreate(view: View) {
    }

    override fun onAttachedToWindow(viewHolder: StateViewHolder) {
    }

    override fun onDetachedFromWindow(viewHolder: StateViewHolder) {
    }
}