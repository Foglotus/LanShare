package com.foglotus.main.base.fragment

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import com.foglotus.core.event.MessageEvent
import com.foglotus.lanshare.core.extension.showToast
import com.foglotus.main.R
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
open class BaseFragment :Fragment(){
    /**
     * 提供loading加载动画
     */
    private var mLoading: ProgressBar?= null

    /**
     * 是否是第一次进入
     */
    var isFirstComing = true


    override fun onResume() {
        super.onResume()
        isFirstComing = false
    }

    /**
     * 布局初始化操作
     */
    open fun onCreateView(view: View):View{
        mLoading = view.findViewById(R.id.loading)
        return view;
    }

    protected fun showLoading(){
        mLoading?.visibility = View.VISIBLE
    }

    protected fun hideLoading(){
        mLoading?.visibility = View.GONE
    }

    protected fun loaded(){
        mLoading?.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(messageEvent: MessageEvent) {
        showToast(messageEvent.toString())
    }

    fun hideKeyboard(event: MotionEvent, view: Array<View>?, activity: Activity):Boolean {
        try {
            view?.forEach {
                if (it is EditText) {
                    val location = intArrayOf(0, 0)
                    it.getLocationInWindow(location)
                    val left = location[0]
                    val top = location[1]
                    val right = left + it.width
                    val bootom = top + it.height
                    // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
                    if (event.rawX < left || event.rawX > right
                        || event.y < top || event.rawY > bootom
                    ) {
                        // 隐藏键盘
                        val token = it.windowToken
                        val inputMethodManager = activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(
                            token,
                            InputMethodManager.HIDE_NOT_ALWAYS
                        )
                        it.clearFocus()
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}