package com.foglotus.main.home.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.extention.logDebug
import com.foglotus.main.base.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_layout.*
import java.util.ArrayList
import android.support.v4.content.ContextCompat
import android.util.SparseArray
import android.widget.LinearLayout
import com.foglotus.core.LanShare
import com.foglotus.core.extention.dp2px
import com.foglotus.main.R
import com.foglotus.core.callback.PendingRunnable
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.server.model.User


/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
class HomeFragment:BaseFragment() {
    //tab页适配器
    private lateinit var pagerAdapter: Adapter
    //当前所在页
    var currentPagerPosition = 0
        private set

    //延迟处理器
    var pendingRunnable = SparseArray<PendingRunnable>()

    lateinit var serverGrantFragment:ServerGrantFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_layout,container,false)
        return super.onCreateView(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews(){
        tabs.setupWithViewPager(viewpager)
        tabs.addOnTabSelectedListener(tabSelectedListener)
        setupViewPager(viewpager)
        (tabs.getChildAt(0) as LinearLayout).apply {
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            dividerDrawable = ContextCompat.getDrawable(
                LanShare.getContext(),
                com.foglotus.main.R.drawable.f_server_manager_tab_divider_vertical
            )
            dividerPadding = dp2px(12f);
        }
    }

    /**
     * 主界面视图元素
     */

    private fun setupViewPager(viewPager: ViewPager) {
        pagerAdapter = Adapter(fragmentManager!!)

        serverGrantFragment = ServerGrantFragment()

        pagerAdapter.addFragment(ServerManagerFragment(), GlobalUtil.getString(R.string.fragment_server_manager_server_tab_server))
        pagerAdapter.addFragment(serverGrantFragment, GlobalUtil.getString(R.string.fragment_server_manager_server_tab_grant))

        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2
        currentPagerPosition = 0

        if (currentPagerPosition < 0 || currentPagerPosition >= pagerAdapter.count) {
            currentPagerPosition = 0
        }
        viewPager.currentItem = currentPagerPosition
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                //执行延迟处理函数
                executePendingRunnable()

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun executePendingRunnable() {
        val fragment = pagerAdapter.getItem(currentPagerPosition)
        if(fragment is ServerGrantFragment){
            executePendingRunnableList()
        }
    }

    private val tabSelectedListener by lazy {
        object : TabLayout.ViewPagerOnTabSelectedListener(viewpager) {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
                if(tab != null){
                    val fragment = pagerAdapter.getItem(tab.position)
                    logDebug("tab点击事件")
                    if(fragment is ServerGrantFragment){
                        fragment.refreshFeeds()
                    }
                }
                println("on tab onTabReselected ${tab?.position}")
            }
        }
    }

    fun addPendingRunnable(key:Int,pending: PendingRunnable){
        pendingRunnable.append(key,pending)
    }

    /**
     * 执行潜在的Pending任务。
     */
    private fun executePendingRunnableList() {
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

    fun receiveNewGrant(user:User){
        val fragment = pagerAdapter.getItem(currentPagerPosition)
        if(fragment == serverGrantFragment){
            serverGrantFragment.receiveNewUser(user)
        }else{
            addPendingRunnable(DataFragment.RECEIVE_UPLOAD,object : PendingRunnable {
                override fun run(index: Int) {
                    receiveNewGrant(user)
                }

            })
        }
    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles[position]
        }
    }
}