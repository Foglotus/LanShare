package com.foglotus.main.download.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foglotus.lanshare.core.extension.showToast
import com.foglotus.lanshare.core.extension.showToastOnUiThread
import com.foglotus.main.R
import com.foglotus.main.download.fragment.FinishFragment
import com.foglotus.network.model.DownloadFile
import com.foglotus.main.mine.util.FileImgUtil
import java.io.File

/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class FinishAdapter(val fragment: FinishFragment,val data:MutableList<DownloadFile>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_download_finish_item,parent,false)
        val holder =  FinishHolder(view)
        holder.del.setOnClickListener {
            val d = data[holder.adapterPosition]
            val file = File(d.path+d.name)
            if(file.exists()){
                file.delete()
                showToastOnUiThread("删除成功")
            }else{
                showToastOnUiThread("文件不存在")
            }
            d.delete()
            data.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            if(data.size == 0 ){
                fragment.showNoContentView("没有下载文件")
            }
        }
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file = data[position]
        val hold = holder as FinishHolder
        hold.name.text = file.name
        hold.des.text = file.getDes()
        hold.path.text = file.path
        hold.typeImg.setImageResource(FileImgUtil.getTypeImg(file.type))
    }

    class FinishHolder(view: View):RecyclerView.ViewHolder(view){
        var rootLayout: LinearLayout = view as LinearLayout
        var typeImg:ImageView = view.findViewById(R.id.typeImg)
        var name:TextView = view.findViewById(R.id.name)
        var path:TextView = view.findViewById(R.id.path)
        var des:TextView = view.findViewById(R.id.des)
        var del:ImageView = view.findViewById(R.id.del)
    }
}