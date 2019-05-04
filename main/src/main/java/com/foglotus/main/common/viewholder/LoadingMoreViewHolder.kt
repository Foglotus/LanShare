package com.foglotus.main.common.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.foglotus.main.R

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
class LoadingMoreViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view)  {
    val progress: ProgressBar = view.findViewById(R.id.loadProgress)

    val end: ImageView = view.findViewById(R.id.loadingEnd)

    val failed: TextView = view.findViewById(R.id.loadFailed)

    companion object {

        fun createLoadingMoreViewHolder(context: Context, parent: ViewGroup): LoadingMoreViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.loading_footer, parent, false)
            return LoadingMoreViewHolder(view)
        }
    }
}