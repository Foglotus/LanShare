package com.foglotus.main.download.fragment

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foglotus.core.LanShare
import com.foglotus.core.extention.logInfo
import com.foglotus.main.R
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.main.common.listener.LoadDataListener
import com.foglotus.main.common.view.SimpleDividerDecoration
import com.foglotus.main.download.adapter.DownloadingAdapter
import com.foglotus.network.model.DownloadFile
import com.foglotus.network.util.DownloadUtil
import kotlinx.android.synthetic.main.fragment_mine_layout.*
import kotlinx.android.synthetic.main.loading.*
import org.litepal.LitePal
import kotlin.concurrent.thread

/**
 *
 * @author foglotus
 * @since 2019/4/17
 */
class DownloadingFragment:DataFragment(),LoadDataListener {

    private lateinit var adapter:DownloadingAdapter
    private val fileList:MutableList<DownloadFile> = ArrayList()
    private var isUpdating = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_downloading_layout,container,false)
        return super.onCreateView(view)
    }

    override fun setRecyclerViews() {
        recyclerView.layoutManager = layoutManager
        adapter = DownloadingAdapter(this,fileList)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(SimpleDividerDecoration(LanShare.getContext()))
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        if(isFirstComing){
            onLoading()
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun refreshFeeds() {
        if(isLoading)return
        onLoading()
        hideNoContentView()
        recyclerView.stopScroll()
        loadData()
    }

    override fun onLoad() {
        isLoadFailed =false
        loadData()
    }

    override fun loadData() {
        isLoading = true
        thread {
            LitePal.updateAll(DownloadFile::class.java,ContentValues().apply { put("model",DownloadFile.PAUSE) },"status != 100 and model != ${DownloadFile.RETRY}")
            val files = LitePal.where("status < 100").order("time asc").find(DownloadFile::class.java)
            handleFetchedData(files,false)
        }
    }

    override fun loadFromDB(isLoadMore:Boolean,lastID:Long){}

    private fun handleFetchedData(files: List<DownloadFile>, isLoadMore: Boolean) {
        activity?.runOnUiThread {
            fileList.clear()
            fileList.addAll(files)
            adapter.notifyDataSetChanged()
            loadFinished()
        }
    }

    override fun onLoading() {
        loading.visibility = View.VISIBLE
    }
    override fun loadFinished() {
        super.loadFinished()
        isLoading = false
        isLoadFailed = false
        hideLoading()
        if(fileList.isEmpty()){
            recyclerView.visibility = View.GONE
            showNoContentView("没有任何上传文件")
        }else{
            recyclerView.visibility = View.VISIBLE
            hideNoContentView()
        }
    }

    fun receiveNewFile(file: DownloadFile){
        isUpdating = true
        activity?.runOnUiThread {
            fileList.add(file)
            adapter.notifyItemInserted(adapter.itemCount)
            logInfo("当前位置",recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)).toString())
            if(fileList.size >= 2 && (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) == 1) ){
                recyclerView.scrollToPosition(0)
            }else{
                refreshFeeds()
            }
            isUpdating = false
        }
    }

    fun downloadFailed(id:Long){
        logInfo("文件下载失败处理")
        var position = -1;
        val find = fileList.find {
            position++
            it.id == id
        }
        if(find != null){
            find.model = DownloadFile.RETRY
            adapter.notifyItemChanged(position)
        }
    }

    fun updateProgress(id:Long,progress:Int){
        var position = -1;
        val find = fileList.find {
            position++
            it.id == id
        }
        if(find != null){
            activity?.runOnUiThread {
                find.status = progress
                if(find.model != DownloadFile.DOWNLOADING)find.model = DownloadFile.DOWNLOADING
                find.save()
                adapter.notifyItemChanged(position)
            }
        }
    }

    fun removeFinish(id:Long){
        isUpdating = true
        var position = -1;
        val find = fileList.find {
            position++
            it.id == id
        }
        if(find != null){
            activity?.runOnUiThread {
                fileList.removeAt(position)
                adapter.notifyItemRemoved(position)
                isUpdating = false
            }
        }
    }

    fun delDownload(id: Long):Boolean {
        DownloadUtil.get().cancel(id)
        return true
    }

    fun handleClick(downloadFile: DownloadFile) {

    }
}