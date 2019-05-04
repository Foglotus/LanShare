package com.foglotus.main.download.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.foglotus.lanshare.core.extension.showToast
import com.foglotus.lanshare.core.extension.showToastOnUiThread
import com.foglotus.main.R
import com.foglotus.main.download.fragment.DownloadingFragment
import com.foglotus.network.model.DownloadFile

/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class DownloadingAdapter(val fragment: DownloadingFragment, val data:MutableList<DownloadFile>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, postion: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_download_downloading_item,parent,false)
        val holder =  DownloadingHolder(view)
        holder.del.setOnClickListener {
            val result = fragment.delDownload(data[holder.adapterPosition].id)
            if(result){
                data[holder.adapterPosition].delete()
                data.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }else{
                showToastOnUiThread("删除失败")
            }
        }
        holder.op.setOnClickListener {
            fragment.handleClick(data[holder.adapterPosition])
        }
        return holder
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, postion: Int) {
        val file = data[postion]
        val hold = holder as DownloadingHolder

        hold.name.text = file.name
        hold.progress.progress = file.status
        hold.status.text = "${file.status}%"
        hold.size.text = file.size
        when(file.model){
            DownloadFile.PAUSE -> hold.op.setImageResource(R.drawable.start)
            DownloadFile.DOWNLOADING -> hold.op.setImageResource(R.drawable.pause)
            DownloadFile.RETRY -> hold.op.setImageResource(R.drawable.retry)
        }
    }

    class DownloadingHolder(view: View):RecyclerView.ViewHolder(view){
        var rootLayout: LinearLayout = view as LinearLayout
        var op: ImageView = view.findViewById(R.id.op)
        var name: TextView = view.findViewById(R.id.name)
        var size:TextView = view.findViewById(R.id.size)
        var progress: ProgressBar = view.findViewById(R.id.progress)
        var status: TextView = view.findViewById(R.id.status)
        var del: ImageView = view.findViewById(R.id.del)
    }

}