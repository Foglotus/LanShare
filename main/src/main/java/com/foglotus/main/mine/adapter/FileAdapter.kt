package com.foglotus.main.mine.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foglotus.core.extention.logInfo
import com.foglotus.main.R
import com.foglotus.main.base.callback.RecycleItemTouchHelper
import com.foglotus.main.common.viewholder.LoadingMoreViewHolder
import com.foglotus.main.mine.fragment.MineFragment
import com.foglotus.main.mine.util.FileImgUtil
import com.foglotus.server.model.File

/**
 *
 * @author foglotus
 * @since 2019/3/22
 */
class FileAdapter(val fragment: MineFragment,val data:MutableList<File>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),RecycleItemTouchHelper.ItemTouchHelperCallback{
    override fun onItemDelete(positon: Int) {
        //不删除最后没有数据的提示
        if(getItemViewType(positon) == TYPE_LOADING_MORE)return
        logInfo("zouzhelimeme")
        val file = data.removeAt(positon)
        file.delete()
        val delFile = java.io.File(file.path)
        if(delFile.exists() && delFile.isFile){
            delFile.delete()
        }
        if(data.size == 0){
            fragment.showNoContentView("没有任何上传文件")
            notifyDataSetChanged()
        }else
            notifyItemRemoved(positon)

    }

    override fun onMove(fromPosition: Int, toPosition: Int) {

    }

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
            TYPE_FILE
        } else TYPE_LOADING_MORE
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            TYPE_FILE -> return onCreateFileViewHolder(parent)
            TYPE_LOADING_MORE -> return onCreateLoadingMoreViewHolder(parent)
        }
        throw IllegalArgumentException()
    }

    private fun onCreateFileViewHolder(parent: ViewGroup):RecyclerView.ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_mine_upload_item,parent,false)
        return FileViewHolder(view)
    }

    private fun onCreateLoadingMoreViewHolder(parent: ViewGroup):RecyclerView.ViewHolder{
        return LoadingMoreViewHolder.createLoadingMoreViewHolder(fragment.activity!!,parent).apply {
            failed.setOnClickListener {
                fragment.onLoad()
                notifyItemChanged(itemCount - 1)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            TYPE_FILE -> bindFileHolder(holder as FileViewHolder, position)
            TYPE_LOADING_MORE -> bindLoadingMoreHolder(holder as LoadingMoreViewHolder)
        }
    }

    private fun bindFileHolder(holder: FileViewHolder,position: Int){
        val item = data[position]
        holder.typeImg.setImageResource(FileImgUtil.getTypeImg(item.fileExtension))
        holder.type.text = item.fileExtension
        holder.name.text = item.fileName
        holder.size.text = item.fileSize
        holder.time.text = item.uploadTime
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

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var rootLayout: LinearLayout = view as LinearLayout

        var typeImg: ImageView = view.findViewById(R.id.typeImg)

        var name: TextView = view.findViewById(R.id.name)

        var type: TextView = view.findViewById(R.id.type)

        var time: TextView = view.findViewById(R.id.time)

        var size: TextView = view.findViewById(R.id.size)
    }

    companion object {
        const val TYPE_FILE = 0
        const val TYPE_LOADING_MORE = 1
    }
}