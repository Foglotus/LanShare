package com.foglotus.main.download.fragment

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
import com.foglotus.main.download.adapter.FinishAdapter
import com.foglotus.network.model.DownloadFile
import kotlinx.android.synthetic.main.fragment_mine_layout.*
import kotlinx.android.synthetic.main.loading.*
import org.litepal.LitePal
import kotlin.concurrent.thread

/**
 *
 * @author foglotus
 * @since 2019/4/17
 */
class FinishFragment :DataFragment(),LoadDataListener{

    private lateinit var adapter:FinishAdapter
    private val fileList:MutableList<DownloadFile> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_finish_layout,container,false)
        return super.onCreateView(view)
    }
    override fun setRecyclerViews() {
        recyclerView.layoutManager = layoutManager
        adapter = FinishAdapter(this,fileList)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(SimpleDividerDecoration(LanShare.getContext()))
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun onResume() {
        if(isFirstComing){
            onLoading()
            loadData()
        }
        super.onResume()
    }
    override fun refreshFeeds() {
        if(isLoading)return
        hideNoContentView()
        recyclerView.stopScroll()
        loadFromDB(false,0)
    }

    override fun onLoad() {
        isLoadFailed =false
        loadData()
    }

    override fun loadData() {
        isLoading = true
        thread {
            val files = LitePal.where("status = ?","100").order("id desc").find(DownloadFile::class.java)
            handleFetchedData(files,false)
        }
    }

    override fun loadFromDB(isLoadMore:Boolean,lastID:Long) {

    }

    private fun handleFetchedData(files: List<DownloadFile>, isLoadMore: Boolean) {
        activity?.runOnUiThread {
            fileList.clear()
            fileList.addAll(files)
            adapter.notifyDataSetChanged()
            loadFinished()
        }
    }

    override fun onLoading() {
        loading.visibility = View.GONE
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
        activity?.runOnUiThread {
            fileList.add(0,file)
            if(fileList.size == 1){
                loadFinished()
            }
            adapter.notifyItemRangeInserted(0,1)
            logInfo("当前位置",recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)).toString())
            if(fileList.size >= 2 && (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) == 1) ){
                recyclerView.scrollToPosition(0)
            }else{
                refreshFeeds()
            }
        }
    }
}