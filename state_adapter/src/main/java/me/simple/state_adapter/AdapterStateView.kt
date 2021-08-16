package me.simple.state_adapter

import android.view.View

interface AdapterStateView {
    fun setLayoutRes(): Int

    fun onCreate(view: View)

    fun onAttachedToWindow(viewHolder: StateViewHolder)

    fun onDetachedFromWindow(viewHolder: StateViewHolder)
}