package com.foglotus.main.monitor.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.foglotus.main.R
import com.foglotus.main.common.viewholder.LoadingMoreViewHolder
import com.foglotus.main.home.adapter.GrantAdapter.Companion.TYPE_GRANT
import com.foglotus.main.monitor.fragment.MonitorFragment
import com.foglotus.server.model.Log

/**
 *
 * @author foglotus
 * @since 2019/3/22
 */
class LogAdapter(val fragment: MonitorFragment,val data:MutableList<Log>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isLoadFailed: Boolean = false
        get() = fragment.isLoadFailed

    private var isNoMoreData: Boolean = false
        get() = fragment.isNoMoreData

    /**
     * 获取RecyclerView数据源中元素的数量。
     * @return RecyclerView数据源中元素的数量。
     */
    private val dataItemCount: Int
        get() = data.size

    /**
     * 视图类型
     */
    override fun getItemViewType(position: Int): Int {
        return if (position < dataItemCount && dataItemCount > 0) {
            TYPE_GRANT
        } else TYPE_LOADING_MORE
    }


    /**
     * 总是比实际多1，是为了显示最后的底部布局
     */
    override fun getItemCount(): Int {
        return data.size+1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            TYPE_LOG -> return onCreateGrantViewHolder(parent)
            TYPE_LOADING_MORE -> return onCreateLoadingMoreViewHolder(parent)
        }
        throw IllegalArgumentException()
    }

    private fun onCreateGrantViewHolder(parent: ViewGroup):RecyclerView.ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_monitor_log_item,parent,false)
        return LogViewHolder(view)
    }

    private fun onCreateLoadingMoreViewHolder(parent: ViewGroup):RecyclerView.ViewHolder{
        return LoadingMoreViewHolder.createLoadingMoreViewHolder(fragment.activity!!,parent).apply {
            failed.setOnClickListener {
                fragment.onLoad()
                notifyItemChanged(itemCount - 1)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            TYPE_LOG -> bindGrantHolder(holder as LogViewHolder, position)
            TYPE_LOADING_MORE -> bindLoadingMoreHolder(holder as LoadingMoreViewHolder)
        }
    }

    private fun bindGrantHolder(holder: LogViewHolder,position: Int){
        val item = data[position]
        holder.desc .text= item.des
        holder.time.text = item.time
        holder.ip.text = item.ip
    }

    private fun bindLoadingMoreHolder(holder: LoadingMoreViewHolder) {
        when {
            isNoMoreData -> {
                holder.progress.visibility = View.GONE
                holder.failed.visibility = View.GONE
                holder.end.visibility = View.VISIBLE
            }
            isLoadFailed -> {
                holder.progress.visibility = View.GONE
                holder.failed.visibility = View.VISIBLE
                holder.end.visibility = View.GONE
            }
            else -> {
                holder.progress.visibility = View.VISIBLE
                holder.failed.visibility = View.GONE
                holder.end.visibility = View.GONE
            }
        }
    }


    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var rootLayout:LinearLayout = view as LinearLayout

        var time:TextView = view.findViewById(R.id.time)

        var ip:TextView = view.findViewById(R.id.ip)

        var desc: TextView = view.findViewById(R.id.description)

    }

    companion object {
        const val TYPE_LOG = 0
        const val TYPE_LOADING_MORE = 1
    }

}