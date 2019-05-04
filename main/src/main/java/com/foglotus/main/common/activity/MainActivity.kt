package com.foglotus.main.common.activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foglotus.core.event.MessageEvent
import com.foglotus.core.extention.logInfo
import com.foglotus.lanshare.core.extension.showToastOnUiThread
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.core.callback.PendingRunnable
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.main.home.fragment.HomeFragment
import com.foglotus.main.monitor.fragment.MonitorFragment
import com.foglotus.main.setting.activity.SettingActivity
import com.foglotus.main.mine.fragment.MineFragment
import com.foglotus.main.util.AnimUtils
import com.foglotus.main.web.fragment.WebFragment
import com.foglotus.server.event.ServerGrantEvent
import com.foglotus.server.event.ServerLogEvent
import com.foglotus.server.event.ServerUploadEvent
import kotlinx.android.synthetic.main.activity_main_layout.*


class MainActivity : BaseActivity(), View.OnClickListener{
    private lateinit var launcherAnimation:AnimationDrawable

    private lateinit var homeFragment:HomeFragment
    private lateinit var monitorFragment: MonitorFragment
    private lateinit var webFragment: WebFragment
    private lateinit var mineFragment: MineFragment

    private lateinit var fragmentManager:FragmentManager

    private lateinit var navViewList:List<TextView>
    private lateinit var navViewImageList:List<Int>
    private lateinit var fragmentList:List<Fragment>

    private lateinit var settingMenuItem: MenuItem

    private var currentPosition = FRAGMENT_HOME



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_layout)
    }

    override fun onResume() {
        super.onResume()
        hideSoftKeyboard()
    }


    override fun setupViews() {
        super.setupViews()
        setupToolbar()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.a_main_toolbar_home_menu)

        launcherAnimation = findViewById<ImageView>(R.id.act_main_bottom_nav_launcher_image_view).drawable as AnimationDrawable

        homeFragment = HomeFragment()
        monitorFragment = MonitorFragment()
        webFragment = WebFragment()
        mineFragment = MineFragment()

        fragmentList = arrayListOf(
            homeFragment,
            monitorFragment,
            webFragment,
            mineFragment
        )

        fragmentManager = supportFragmentManager


        navViewList = arrayListOf(
            act_main_bottom_nav_home_image_view,
            act_main_bottom_nav_monitor_image_view,
            act_main_bottom_nav_web_image_view,
            act_main_bottom_nav_mine_image_view
        )
        navViewImageList = arrayListOf(
            R.drawable.a_main_bottom_nav_home,
            R.drawable.a_main_bottom_nav_monitor,
            R.drawable.a_main_bottom_nav_web,
            R.drawable.a_main_bottom_nav_mine
        )

        fragmentManager.beginTransaction().apply {
            add(R.id.act_main_content_layout,homeFragment)
            add(R.id.act_main_content_layout,monitorFragment)
            hide(monitorFragment)
            add(R.id.act_main_content_layout,webFragment)
            hide(webFragment)
            add(R.id.act_main_content_layout,mineFragment)
            hide(mineFragment)
            commit()
        }

        act_main_bottom_nav_home_image_view.setBackgroundResource(R.drawable.a_main_bottom_nav_home_selected)

        act_main_bottom_nav_home.setOnClickListener(this)
        act_main_bottom_nav_web.setOnClickListener(this)
        act_main_bottom_nav_monitor.setOnClickListener(this)
        act_main_bottom_nav_mine.setOnClickListener(this)
        animateToolbar()
    }

    /**
     * 点击事件
     */
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.act_main_bottom_nav_home -> {
                if(currentPosition == FRAGMENT_HOME)return
                act_main_bottom_nav_home_image_view.setBackgroundResource(R.drawable.a_main_bottom_nav_home_selected)
                navViewList[currentPosition].setBackgroundResource(navViewImageList[currentPosition])
                toolbarTitle.text = getText(R.string.act_main_bottom_nav_home)
                showFragment(homeFragment, FRAGMENT_HOME)
            }
            R.id.act_main_bottom_nav_monitor ->{
                if(currentPosition == FRAGMENT_MONITOR)return
                act_main_bottom_nav_monitor_image_view.setBackgroundResource(R.drawable.a_main_bottom_nav_monitor_selected)
                navViewList[currentPosition].setBackgroundResource(navViewImageList[currentPosition])
                toolbarTitle.text = getText(R.string.act_main_bottom_nav_monitor)
                showFragment(monitorFragment, FRAGMENT_MONITOR)
            }
            R.id.act_main_bottom_nav_web ->{
                if(currentPosition == FRAGMENT_WEB)return
                act_main_bottom_nav_web_image_view.setBackgroundResource(R.drawable.a_main_bottom_nav_web_selected)
                navViewList[currentPosition].setBackgroundResource(navViewImageList[currentPosition])
                toolbarTitle.text = getText(R.string.act_main_bottom_nav_web)
                showFragment(webFragment, FRAGMENT_WEB)
            }
            R.id.act_main_bottom_nav_mine ->{
                if(currentPosition == FRAGMENT_MINE)return
                settingMenuItem.isVisible = true
                act_main_bottom_nav_mine_image_view.setBackgroundResource(R.drawable.a_main_bottom_nav_mine_selected)
                navViewList[currentPosition].setBackgroundResource(navViewImageList[currentPosition])
                toolbarTitle.text = getText(R.string.act_main_bottom_nav_mine)
                showFragment(mineFragment, FRAGMENT_MINE)
            }
        }
        executePendingRunnable()
    }

    private fun showFragment(fragment:Fragment,from:Int){
        val ft = fragmentManager.beginTransaction()
        if(currentPosition == FRAGMENT_MINE)settingMenuItem.isVisible = false
        ft.hide(fragmentList[currentPosition])
        ft.show(fragment)
        ft.commit()
        currentPosition = from
        animateToolbar()
    }

    /**
     * fragment页面的延迟任务队列处理
     */
    private fun executePendingRunnable(){
        val fragment = fragmentList[currentPosition]
        if(fragment is DataFragment){
            fragment.executePendingRunnableList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                showToastOnUiThread("点击了")
            }
            R.id.menu_setting ->{
                SettingActivity.actionStart(this)
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        settingMenuItem = menu!!.findItem(R.id.menu_setting)!!.apply {
            isVisible = false
        }
        return true
    }

    override fun onMessageEvent(messageEvent: MessageEvent) {

        when(messageEvent){
            is ServerUploadEvent -> {
                logInfo("收到上传文件事件")
                launcherAnimation.start()
                if(currentPosition != MainActivity.FRAGMENT_MINE){
                    mineFragment.addPendingRunnable(DataFragment.RECEIVE_UPLOAD,object :
                        PendingRunnable {
                        override fun run(index: Int) {
                            mineFragment.receiveNewFile(messageEvent.file)
                        }

                    })
                }else{
                    mineFragment.receiveNewFile(messageEvent.file)
                }
            }
            is ServerGrantEvent -> {
                logInfo("收到grant事件")
                launcherAnimation.start()
                if(currentPosition != MainActivity.FRAGMENT_HOME){
                    homeFragment.addPendingRunnable(DataFragment.REVEIVE_GRANT,object :
                        PendingRunnable {
                        override fun run(index: Int) {
                            homeFragment.receiveNewGrant(messageEvent.grant)
                        }

                    })
                }else{
                    homeFragment.receiveNewGrant(messageEvent.grant)
                }
            }
            is ServerLogEvent -> {
                logInfo("收到log事件")
                launcherAnimation.start()
                if(currentPosition != MainActivity.FRAGMENT_MONITOR){
                    monitorFragment.addPendingRunnable(DataFragment.RECEIVE_LOG,object:
                        PendingRunnable {
                        override fun run(index: Int) {
                            monitorFragment.receiveNewLog(messageEvent.log)
                        }

                    })
                }else{
                    monitorFragment.receiveNewLog(messageEvent.log)
                }
            }
            else -> super.onMessageEvent(messageEvent)
        }
    }

    /**
     * 使用缩放动画的方式将Toolbar标题显示出来。
     */
    private fun animateToolbar() {
        val t = toolbar?.getChildAt(0)
        if (t != null && t is TextView) {
            t.alpha = 0f
            t.scaleX = 0.8f
            t.animate()
                .alpha(1f)
                .scaleX(1f)
                .setStartDelay(100)
                .setDuration(700).interpolator = AnimUtils.getFastOutSlowInInterpolator(this)
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val FRAGMENT_HOME = 0
        const val FRAGMENT_MONITOR = 1
        const val FRAGMENT_WEB = 2
        const val FRAGMENT_MINE = 3
        fun actionStart(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
