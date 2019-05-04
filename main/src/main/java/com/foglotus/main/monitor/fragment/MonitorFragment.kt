package com.foglotus.main.monitor.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foglotus.core.LanShare
import com.foglotus.core.extention.logInfo
import com.foglotus.main.R
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.main.common.listener.InfiniteScrollListener
import com.foglotus.main.common.listener.LoadDataListener
import com.foglotus.main.common.view.SimpleDividerDecoration
import com.foglotus.main.monitor.adapter.LogAdapter
import com.foglotus.server.model.Log
import kotlinx.android.synthetic.main.fragment_monitor_layout.*
import kotlinx.android.synthetic.main.loading.*
import org.litepal.LitePal
import kotlin.concurrent.thread

/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
class MonitorFragment:DataFragment() ,LoadDataListener{


    private val logList:MutableList<Log> = ArrayList()
    private lateinit var adapter:LogAdapter


    private var rootView:View ?= null

    //没有任何数据展示
    private var noContentView:View ?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_monitor_layout,container,false)
        rootView = view
        return super.onCreateView(view)
    }

    override fun onResume() {
        if(isFirstComing){
            onLoading()
            loadData()
        }
        super.onResume()
    }
    override fun setRecyclerViews(){
        layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
        adapter = LogAdapter(this,logList)
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
        swipeRefresh.setOnRefreshListener { refreshFeeds() }
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

    override fun onLoading(){
        if(logList.isEmpty()){
            swipeRefresh.visibility = View.GONE
            loading.visibility = View.VISIBLE
        }else{
            loading.visibility = View.GONE
        }
    }

    override fun loadFromDB(isLoadMore:Boolean,last:Long){
        thread {
            val logs = if(isLoadMore){
                LitePal.limit(10).where("id < ?","$last").order("id desc").find(Log::class.java)
            }else{
                LitePal.limit(10).order("id desc").find(Log::class.java)
            }
            isNoMoreData = (logs.size < 10)
            handleFetchedData(logs,isLoadMore)
        }
    }

    override fun loadData() {
        isLoading = true
        var lastLog:Long = 0
        val isLoadMore:Boolean
        if(!logList.isEmpty()){
            lastLog = logList[logList.size-1].id
            isLoadMore = true
        }else{
            isLoadMore = false
        }

        loadFromDB(isLoadMore,lastLog)
    }

    private fun handleFetchedData(logs: List<Log>, isLoadMore: Boolean) {
        if(logs.isEmpty()){
            activity?.runOnUiThread {
                adapter.notifyItemChanged(adapter.itemCount -1)
                loadFinished()
            }
        }else{
            if(isLoadMore){
                activity?.runOnUiThread {
                    recyclerView.stopScroll()
                    logList.addAll(logs)
                    adapter.notifyDataSetChanged()
                    loadFinished()
                }
            }else{
                activity?.runOnUiThread {
                    logList.clear()
                    logList.addAll(logs)
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(0)
                    loadFinished()
                }
            }

        }
    }

    fun receiveNewLog(log:Log){
        logList.add(0,log)
        adapter.notifyItemRangeInserted(0,1)
        logInfo("当前位置",recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)).toString())
        if(logList.size >= 2 && (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) == 1) ){
            recyclerView.scrollToPosition(0)
        }else{
            refreshFeeds()
        }
    }

    override fun loadFinished() {
        isLoading = false
        isLoadFailed = false
        loading.visibility = View.GONE
        if(logList.isEmpty()){
            recyclerView.visibility = View.GONE
            swipeRefresh.visibility = View.GONE
            showNoContentView("没有日志")
        }else{
            recyclerView.visibility = View.VISIBLE
            swipeRefresh.visibility = View.VISIBLE
            noContentView?.visibility = View.GONE
        }
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
    }
}