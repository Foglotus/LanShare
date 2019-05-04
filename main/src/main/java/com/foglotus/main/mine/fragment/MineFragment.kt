package com.foglotus.main.mine.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foglotus.core.LanShare
import com.foglotus.core.util.FileUtil
import com.foglotus.core.extention.logInfo
import com.foglotus.lanshare.core.extension.showToastOnUiThread
import com.foglotus.main.R
import com.foglotus.main.base.callback.OnRecyclerItemClickListener
import com.foglotus.main.base.callback.RecycleItemTouchHelper
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.main.common.listener.InfiniteScrollListener
import com.foglotus.main.common.listener.LoadDataListener
import com.foglotus.main.common.view.SimpleDividerDecoration
import com.foglotus.main.mine.adapter.FileAdapter
import com.foglotus.server.model.File
import kotlinx.android.synthetic.main.fragment_mine_layout.*
import kotlinx.android.synthetic.main.loading.*
import org.litepal.LitePal
import kotlin.concurrent.thread


/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
class MineFragment:DataFragment(),LoadDataListener {

    private lateinit var adapter:FileAdapter
    private val fileList:MutableList<File> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mine_layout,container,false)
        return super.onCreateView(view)
    }
    override fun setRecyclerViews() {
        recyclerView.layoutManager = layoutManager
        adapter = FileAdapter(this,fileList)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(SimpleDividerDecoration(LanShare.getContext()))
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView.addOnScrollListener(object : InfiniteScrollListener(layoutManager) {
            override fun onLoadMore() {
                onLoad()
            }
            override fun isDataLoading() = isLoading

            override fun isNoMoreData() = isNoMoreData

        })
        recyclerView.addOnItemTouchListener(object : OnRecyclerItemClickListener(recyclerView){
            override fun onItemClick(vh: RecyclerView.ViewHolder?) {
                if(vh!=null){
                    val file = java.io.File(fileList[vh.adapterPosition].path)
                    if(file.exists()){
                        FileUtil.openFile(file,LanShare.getContext())
                    }else{
                        showToastOnUiThread("文件不存在!")
                    }
                }
            }

            override fun onItemLongClick(vh: RecyclerView.ViewHolder?) {

            }

        })

        val callback = RecycleItemTouchHelper(adapter as RecycleItemTouchHelper.ItemTouchHelperCallback);
        val itemTouchHelper=ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefresh.setOnRefreshListener { refreshFeeds() }
    }

    override fun onResume() {
        if(isFirstComing){
            onLoading()
            loadData()
        }
        super.onResume()
    }

    override fun refreshFeeds() {
        isLoading = true
        hideNoContentView()
        recyclerView.stopScroll()
        swipeRefresh.visibility = View.VISIBLE
        if(!swipeRefresh.isRefreshing){
            swipeRefresh.isRefreshing = true
        }
        loadFromDB(false,0)
    }

    override fun onLoad() {
        isNoMoreData = false
        isLoadFailed =false
        loadData()
    }

    override fun loadData() {
        isLoading = true
        var lastFile:Long = 0
        val isLoadMore:Boolean
        if(fileList.isNotEmpty()){
            lastFile = fileList[fileList.size-1].id
            isLoadMore = true
        }else{
            isLoadMore = false
        }
        loadFromDB(isLoadMore,lastFile)
    }

    override fun loadFromDB(isLoadMore:Boolean,lastID:Long) {
        thread {
            val files = if(isLoadMore){
                LitePal.limit(10).where("id < ?","$lastID").order("id desc").find(File::class.java)
            }else{
                LitePal.limit(10).order("id desc").find(File::class.java)
            }
            isNoMoreData = (files.size < 10)
            handleFetchedData(files,isLoadMore)
        }
    }

    private fun handleFetchedData(files: List<File>, isLoadMore: Boolean) {
        if(files.isEmpty()){
            activity?.runOnUiThread {
                adapter.notifyItemChanged(adapter.itemCount -1)
                loadFinished()
            }
        }else{
            if(isLoadMore){
                activity?.runOnUiThread {
                    recyclerView.stopScroll()
                    fileList.addAll(files)
                    adapter.notifyDataSetChanged()
                    loadFinished()
                }
            }else{
                activity?.runOnUiThread {
                    fileList.clear()
                    fileList.addAll(files)
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(0)
                    loadFinished()
                }
            }
        }
    }

    override fun onLoading() {
        if(fileList.isEmpty()){
            swipeRefresh.visibility = View.GONE
            loading.visibility = View.VISIBLE
        }else{
            loading.visibility = View.GONE
        }
    }

    override fun loadFinished() {
        super.loadFinished()
        isLoading = false
        isLoadFailed = false
        hideLoading()
        if(fileList.isEmpty()){
            recyclerView.visibility = View.GONE
            swipeRefresh.visibility = View.GONE
            showNoContentView("没有任何上传文件")
        }else{
            recyclerView.visibility = View.VISIBLE
            swipeRefresh.visibility = View.VISIBLE
            hideNoContentView()
        }
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }

    }

    fun receiveNewFile(file:File){
        fileList.add(0,file)
        adapter.notifyItemRangeInserted(0,1)
        logInfo("当前位置",recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)).toString())
        if(fileList.size >= 2 && (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) == 1) ){
            recyclerView.scrollToPosition(0)
        }else{
            refreshFeeds()
        }

    }

    companion object {
        const val REQUEST_FILE_PATH = 10000
    }
}