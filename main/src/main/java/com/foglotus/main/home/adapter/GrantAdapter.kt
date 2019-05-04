package com.foglotus.main.home.adapter

import android.content.ContentValues
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.foglotus.core.extention.logInfo
import com.foglotus.main.R
import com.foglotus.main.common.viewholder.LoadingMoreViewHolder
import com.foglotus.main.home.fragment.ServerGrantFragment
import com.foglotus.server.model.User
import org.litepal.LitePal

/**
 *
 * @author foglotus
 * @since 2019/3/18
 */
class GrantAdapter(val fragment:ServerGrantFragment,val data:MutableList<User>) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){


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
            TYPE_GRANT -> return onCreateGrantViewHolder(parent)
            TYPE_LOADING_MORE -> return onCreateLoadingMoreViewHolder(parent)
        }
        throw IllegalArgumentException()
    }

    private fun onCreateGrantViewHolder(parent: ViewGroup):RecyclerView.ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_home_server_grant_item,parent,false)
        val holder = GrantViewHolder(view)
        holder.grant.setOnClickListener {
            val position = holder.adapterPosition
            val user = data[position]
            user.status = !user.status
            LitePal.update(User::class.java, ContentValues().apply {
                put("status",user.status)
            },user.id)
            logInfo(LitePal.find(User::class.java,user.id).toString())
            notifyItemChanged(position)
        }
        return holder
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
            TYPE_GRANT -> bindGrantHolder(holder as GrantViewHolder, position)
            TYPE_LOADING_MORE -> bindLoadingMoreHolder(holder as LoadingMoreViewHolder)
        }
    }

    private fun bindGrantHolder(holder: GrantViewHolder,position: Int){
        val item = data[position]
        if(item.status){
            holder.grant.setBackgroundResource(R.drawable.server_granted_bg)
        }else{
            holder.grant.setBackgroundResource(R.drawable.server_grant_bg)
        }
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


    class GrantViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var rootLayout:LinearLayout = view as LinearLayout

        var grant:Button = view.findViewById(R.id.grantButton)

        var time:TextView = view.findViewById(R.id.grantTime)

        var ip:TextView = view.findViewById(R.id.ip)

        var desc:TextView = view.findViewById(R.id.description)

    }

    companion object {
        const val TYPE_GRANT = 0
        const val TYPE_LOADING_MORE = 1
    }

}