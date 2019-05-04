package com.foglotus.main.download.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.MenuItem
import com.foglotus.core.event.MessageEvent
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.extention.logDebug
import com.foglotus.core.extention.logInfo
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.core.callback.PendingRunnable
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.main.download.broadcast.UpdateProgressBroadcast
import com.foglotus.main.download.event.AddDownloadEvent
import com.foglotus.main.download.fragment.DownloadingFragment
import com.foglotus.main.download.fragment.FinishFragment
import com.foglotus.network.event.DownloadFailedEvent
import com.foglotus.network.event.DownloadFinishEvent
import com.foglotus.network.util.DownloadUtil.Companion.INTENT_FILTER_PROGRESS
import kotlinx.android.synthetic.main.activity_download_layout.*
import java.util.*

/**
 *
 * @author foglotus
 * @since 2019/4/17
 */
class DownloadActivity:BaseActivity() {
    //广播
    private var updateProgressBroadcast: UpdateProgressBroadcast ?=null
    //tab页适配器
    private lateinit var pagerAdapter: Adapter

    private lateinit var finishFragment: FinishFragment

    private lateinit var downloadingFragment: DownloadingFragment

    private lateinit var fragmentList: List<DataFragment>


    //当前所在页
    var currentPagerPosition = 0
        private set
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_layout)
        if(updateProgressBroadcast == null){
            updateProgressBroadcast = UpdateProgressBroadcast(this)
        }
        updateProgressBroadcast?.registerAction(INTENT_FILTER_PROGRESS)
    }

    override fun setupViews() {
        super.setupViews()
        setupToolbar()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        tabs.setupWithViewPager(viewpager)
        //tabs.addOnTabSelectedListener(tabSelectedListener)
        setupViewPager(viewpager)
    }

    /**
     * 主界面视图元素
     */

    private fun setupViewPager(viewPager: ViewPager) {
        pagerAdapter = Adapter(supportFragmentManager)

        finishFragment = FinishFragment()
        downloadingFragment = DownloadingFragment()

        fragmentList = arrayListOf(finishFragment,downloadingFragment)

        pagerAdapter.addFragment(finishFragment, GlobalUtil.getString(R.string.activity_download_tab_finish))
        pagerAdapter.addFragment(downloadingFragment, GlobalUtil.getString(R.string.activity_download_tab_downloading))

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
                executePendingRunnable()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        executePendingRunnable()
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.backStackEntryCount
        logDebug(TAG, "fragments is $fragments")
        if (fragments == 1) {
            finish()
        } else {
            if (fragments > 1) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
        }
        return super.onOptionsItemSelected(item)
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

    fun updateProgress(id:Long,progress:Int){
        val fragment = pagerAdapter.getItem(DOWNLOADING_FRAGMENT) as DownloadingFragment
        if(id >0)
            fragment.updateProgress(id,progress)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateProgressBroadcast?.unRegisterAction()
    }


    override fun onMessageEvent(messageEvent: MessageEvent) {
        logInfo("$TAG 收到事件")
        when(messageEvent){
            is DownloadFinishEvent -> {
                if(finishFragment.isShowing){
                    logInfo("显示于界面，直接处理")
                    finishFragment.receiveNewFile(messageEvent.file)
                }else{
                    logInfo("界面未显示，延迟处理")
                    finishFragment.pendingRunnable.append(messageEvent.file.id.toInt(),object:
                        PendingRunnable {
                        override fun run(index: Int) {
                            finishFragment.receiveNewFile(messageEvent.file)
                        }
                    })
                }

                if(downloadingFragment.isShowing){
                    downloadingFragment.removeFinish(messageEvent.file.id)
                }else{
                    downloadingFragment.pendingRunnable.append(messageEvent.file.id.toInt(),object:
                        PendingRunnable {
                        override fun run(index: Int) {
                            downloadingFragment.removeFinish(messageEvent.file.id)
                        }

                    })
                }

            }
            is DownloadFailedEvent ->{
                downloadingFragment.downloadFailed(messageEvent.id)
            }
            is AddDownloadEvent -> {
                if(downloadingFragment.isFirstComing){
                    logInfo("下载页面还未第一次显示， 所以不执行")
                    return
                }
                if(downloadingFragment.isShowing){
                    logInfo("下载页面显示状态，直接添加")
                    downloadingFragment.receiveNewFile(messageEvent.downloadFile)
                }else{
                    logInfo("下载页面没有显示，添加延迟函数")
                    downloadingFragment.pendingRunnable.append(messageEvent.downloadFile.id.toInt(),object:
                        PendingRunnable {
                        override fun run(index: Int) {
                            downloadingFragment.receiveNewFile(messageEvent.downloadFile)
                        }

                    })
                }
            }
            else ->{
                super.onMessageEvent(messageEvent)
            }
        }

    }

    private fun executePendingRunnable(){
        val fragment = fragmentList[currentPagerPosition]
        fragment.executePendingRunnableList()
    }

    companion object {
        const val TAG ="DownloadActivity"
        const val FINISH_FRAGMENT = 0
        const val DOWNLOADING_FRAGMENT = 1
        fun actionStart(activity: Activity) {
            val intent = Intent(activity, DownloadActivity::class.java)
            activity.startActivity(intent)
        }
    }
}