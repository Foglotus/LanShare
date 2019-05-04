package com.foglotus.main.web.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foglotus.core.util.FileUtil
import com.foglotus.core.util.GlobalUtil
import com.foglotus.main.R
import com.foglotus.main.mine.util.FileImgUtil
import com.foglotus.main.web.fragment.WebFragment
import com.foglotus.network.model.File

/**
 *
 * @author foglotus
 * @since 2019/3/22
 */
class FileAdapter(val fragment: WebFragment, val data:MutableList<File>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_web_file_item,parent,false)
        val holder = FileViewHolder(view)
//        holder.rootLayout.setOnClickListener {
//            val position = holder.adapterPosition
//            val item = data[position]
//            fragment.handleClick(item)
//        }
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,position: Int) {
        val v = holder as FileViewHolder
        val item = data[position]
        v.typeImg.setImageResource(FileImgUtil.getTypeImg(item.type))
        v.name.text = item.name
        v.type.text = if(item.type.isEmpty())"未知" else item.type
        v.size.text = FileUtil.getConvertSize(item.size.toLong())

    }

    class FileViewHolder(view: View):RecyclerView.ViewHolder(view){
        val rootLayout:LinearLayout = view as LinearLayout
        var typeImg: ImageView = view.findViewById(R.id.typeImg)

        var name: TextView = view.findViewById(R.id.name)

        var type: TextView = view.findViewById(R.id.type)

        var size:TextView = view.findViewById(R.id.size)
    }

}