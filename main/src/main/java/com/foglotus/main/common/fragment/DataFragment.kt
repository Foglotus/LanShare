package com.foglotus.main.common.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.SparseArray
import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.TextView
import com.foglotus.main.R
import com.foglotus.core.callback.PendingRunnable
import com.foglotus.main.base.fragment.BaseFragment

/**
 *
 * @author foglotus
 * @since 2019/3/22
 */
abstract class DataFragment:BaseFragment() {

    lateinit var layoutManager: LinearLayoutManager
    /**
     * 加载失败
     */
    var isLoadFailed: Boolean = false
    /**
     * 没有更多数据
     */
    var isNoMoreData:Boolean = false

    /**
     * 判断是否正在加载Feeds。
     */
    var isLoading = false

    //没有任何数据展示
    private var noContentView: View?= null

    private lateinit var rootView:View

    var isShowing = false

    //延迟处理器
    var pendingRunnable = SparseArray<PendingRunnable>()


    /**
     * 初始化视图元素由，子类调用
     */
    override fun onCreateView(view: View): View {
        super.onCreateView(view)
        rootView = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(this.activity)
        setRecyclerViews()
    }

    /**
     * 提供初始化recyclerView方法,子类重写，有父类自动调用
     */
    abstract fun setRecyclerViews()

    /**
     * loading
     */
    fun showNoContentView(tip:String){
        if(noContentView != null){
            noContentView?.findViewById<TextView>(R.id.noContentText)?.text = tip
            noContentView!!.visibility = View.VISIBLE
            return
        }
        val viewStub = rootView.findViewById<ViewStub>(R.id.noContentView)
        if(viewStub != null){
            noContentView = viewStub.inflate()
            noContentView?.visibility = View.VISIBLE
            noContentView?.findViewById<ImageView>(R.id.refresh)?.setOnClickListener {
                if(!isLoading)
                    refreshFeeds()
            }
            noContentView?.findViewById<TextView>(R.id.noContentText)?.text = tip
        }
    }

    /**
     * 执行潜在的Pending任务。
     */
    fun executePendingRunnableList() {
        val size = pendingRunnable.size()
        if (size > 0) {
            for (i in 0 until size) {
                val index = pendingRunnable.keyAt(i)
                val runnable = pendingRunnable.get(index)
                runnable.run(index)
            }
            pendingRunnable.clear()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isShowing = userVisibleHint
    }

    fun addPendingRunnable(key:Int,pending: PendingRunnable){
        pendingRunnable.append(key,pending)
    }

    abstract fun refreshFeeds()
    abstract fun loadData()
    abstract fun loadFromDB(isLoadMore:Boolean,lastID:Long)
    abstract fun onLoading()

    open fun loadFinished(){
        hideLoading()
    }

    fun hideNoContentView(){
        noContentView?.visibility = View.GONE
    }

    companion object {
        const val RECEIVE_UPLOAD = 1
        const val RECEIVE_LOG = 2
        const val REVEIVE_GRANT = 3
    }
}