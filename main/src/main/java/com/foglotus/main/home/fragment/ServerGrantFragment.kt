package com.foglotus.main.home.fragment

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
import com.foglotus.main.home.adapter.GrantAdapter
import com.foglotus.server.model.User
import kotlinx.android.synthetic.main.fragment_home_server_grant_layout.*
import kotlinx.android.synthetic.main.loading.*
import org.litepal.LitePal
import kotlin.concurrent.thread

/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
class ServerGrantFragment:DataFragment() ,LoadDataListener{


    private lateinit var adapter:GrantAdapter

    var grantList:MutableList<User> = ArrayList()


    //没有任何数据展示
    private var noContentView:View ?= null

    private var rootView:View ?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_server_grant_layout,container,false)
        view.setOnTouchListener { v, event ->
            hideKeyboard(event, arrayOf(search_tag_input_edit),activity!!)
        }
        rootView = view
        return super.onCreateView(view)
    }

    override fun onCreateView(view: View): View {
        return super.onCreateView(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerViews()
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
        adapter = GrantAdapter(this,grantList)
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
        if(grantList.isEmpty()){
            swipeRefresh.visibility = View.GONE
            loading.visibility = View.VISIBLE
        }else{
            loading.visibility = View.GONE
        }
    }

    override fun loadFromDB(isLoadMore:Boolean,last:Long){
        thread {
            val users = if(isLoadMore){
                 LitePal.limit(10).where("id < ?","$last").order("id desc").find(User::class.java)
            }else{
                LitePal.limit(10).order("id desc").find(User::class.java)
            }
            isNoMoreData = (users.size < 10)
            handleFetchedData(users,isLoadMore)
            isLoading = false
        }
    }

    private fun handleFetchedData(users: List<User>, isLoadMore: Boolean) {
        if(users.isEmpty()){
            activity?.runOnUiThread {
                adapter.notifyItemChanged(adapter.itemCount -1)
                loadFinished()
            }
        }else{
            if(isLoadMore){
                activity?.runOnUiThread {
                    recyclerView.stopScroll()
                    grantList.addAll(users)
                    adapter.notifyDataSetChanged()
                    loadFinished()
                }

            }else{
                activity?.runOnUiThread {
                    grantList.clear()
                    grantList.addAll(users)
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(0)
                    loadFinished()
                }
            }
        }
    }

    override fun loadFinished() {
        isLoadFailed = false
        loading.visibility = View.GONE
        if(grantList.isEmpty()){
            recyclerView.visibility = View.GONE
            swipeRefresh.visibility = View.GONE
            showNoContentView("没有用户")
        }else{
            recyclerView.visibility = View.VISIBLE
            swipeRefresh.visibility = View.VISIBLE
            noContentView?.visibility = View.GONE
        }
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
    }

    fun receiveNewUser(user:User){
        grantList.add(0,user)
        adapter.notifyItemRangeInserted(0,1)
        logInfo("当前位置",recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)).toString())
        if(grantList.size >= 2 && (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) == 1) ){
            recyclerView.scrollToPosition(0)
        }else{
            refreshFeeds()
        }
    }

    override fun loadData() {
        isLoading = true
        var lastUserGrant:Long = 0
        val isLoadMore:Boolean
        if(!grantList.isEmpty()){
            lastUserGrant = grantList[grantList.size-1].id
            isLoadMore = true
        }else{
            isLoadMore = false
        }
        loadFromDB(isLoadMore,lastUserGrant)
        isLoading = false
    }
}