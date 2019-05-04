package com.foglotus.main.home.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.foglotus.core.Const
import com.foglotus.core.LanShare
import com.foglotus.core.dialog.SweetAlertDialog
import com.foglotus.core.event.MessageEvent
import com.foglotus.core.util.DateUtil
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.util.NetworkUtil
import com.foglotus.core.util.SharedUtil
import com.foglotus.core.extention.logWarn
import com.foglotus.main.R
import com.foglotus.main.base.fragment.BaseFragment
import com.foglotus.main.home.util.ServerUtil
import com.foglotus.server.event.NetWorkStateReceiver
import com.foglotus.server.event.NetworkChangeEvent
import com.foglotus.server.event.ServerRunningEvent
import com.foglotus.server.event.ServerStatusChangeEvent
import com.foglotus.server.service.ServerService
import kotlinx.android.synthetic.main.fragment_home_server_manager_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.random.Random

/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
class ServerManagerFragment :BaseFragment(){
    private val runnable = object :Runnable{
        override fun run() {
            updateColor(Random(System.currentTimeMillis()).nextInt(7))
            LanShare.getHandler().postDelayed(this,500)
        }

    }
    var dialog:SweetAlertDialog ?= null
    private val serverIntent by lazy {
        Intent(LanShare.getContext(), ServerService::class.java)
    }

    private val netWorkStateReceiver by lazy {
        NetWorkStateReceiver()
    }


    private val btnStartSelector by lazy {
        this.resources.getDrawable(R.drawable.f_server_manager_btn_server_start_selector)
    }

    private val btnStopSelector by lazy {
        this.resources.getDrawable(R.drawable.f_server_manager_btn_server_stop_selector)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_server_manager_layout, container, false)
        view.setOnTouchListener { v, event ->
            hideKeyboard(event, arrayOf(serverPort,serverTimeout),activity!!)
        }
        EventBus.getDefault().register(this)
        return onCreateView(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    private fun serverRunOption(tip: String){
        dialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        dialog?.titleText = tip
        dialog?.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
        dialog?.show()
        LanShare.getHandler().postDelayed(runnable,500)
    }


    private fun serverOptionSuccess(){
        dialog?.titleText = "Success!"
        dialog?.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
        LanShare.getHandler().removeCallbacks(runnable)
        LanShare.getHandler().postDelayed(Runnable {
            dialog?.dismissWithAnimation()
        },1200)
    }

    private fun serverOptionFailed(){
        dialog?.titleText = "Failed!"
        dialog?.changeAlertType(SweetAlertDialog.ERROR_TYPE)
        LanShare.getHandler().removeCallbacks(runnable)
        LanShare.getHandler().postDelayed(Runnable {
            dialog?.dismissWithAnimation()
        },1200)
    }

    private fun updateColor(i:Int){
        when (i) {
            0 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.blue_btn_bg_color)
            1 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.material_deep_teal_50)
            2 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.success_stroke_color)
            3 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.material_deep_teal_20)
            4 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.material_blue_grey_80)
            5 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.warning_stroke_color)
            6 -> dialog?.progressHelper?.barColor = resources.getColor(R.color.success_stroke_color)
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //启动网络监测
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        activity?.registerReceiver(netWorkStateReceiver, filter)



        serverAddress.text = NetworkUtil.getDeviceIP()

        dynamicCode.setOnCheckedChangeListener { _, isChecked -> SharedUtil.save(Const.Server.DYNAMIC_SAFE_CODE,!isChecked) }
        SharedUtil.save(Const.Server.DYNAMIC_SAFE_CODE,!dynamicCode.isChecked)
        safeCode.text = GlobalUtil.getSafeCode(true)

        btnServerOption.setOnClickListener {

            serverTimeout.clearFocus()
            serverPort.clearFocus()

            serverRunOption(btnServerOption.text.toString())
            val imm = LanShare.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            when(btnServerOption.text == "启动服务"){
                true -> {
                    var port = if(TextUtils.isEmpty(serverPort.text)) SharedUtil.read("default_port",ServerUtil.getServerDefaultPort()) else serverPort.text.toString().toInt()
                    var timeout = if(TextUtils.isEmpty(serverTimeout.text)) SharedUtil.read("default_timeout",ServerUtil.getServerDefaultTimeout()) else serverTimeout.text.toString().toInt()
                    serverIntent.putExtra("host",serverAddress.text.toString())
                    serverIntent.putExtra("port",port)
                    serverIntent.putExtra("timeout",timeout)
                    serverIntent.putExtra("uploadPath",ServerUtil.getServerDefaultUploadPath())
                    activity!!.startService(serverIntent)
                }
                false -> {
                    activity!!.stopService(serverIntent)
                    safeCode.text = GlobalUtil.getSafeCode(true)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        activity?.unregisterReceiver(netWorkStateReceiver)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        if (messageEvent is ServerStatusChangeEvent) {
            logWarn("收到事件")
            when (messageEvent.status) {
                true -> onServerStarted()
                false -> onServerStopped()
            }
        }else if(messageEvent is ServerRunningEvent){
            when (messageEvent.status) {
                true -> LanShare.getHandler().postDelayed(
                    Runnable {  serverOptionSuccess()},1000)
                false -> LanShare.getHandler().postDelayed(
                    Runnable {  serverOptionFailed()},1000)
            }
        }else if(messageEvent is NetworkChangeEvent){

            onNetworkStatusChange(messageEvent.level)
        }
    }

    private fun onHostChange(){

    }


    private fun onNetworkStatusChange(level:Int){
        serverAddress.text = NetworkUtil.getDeviceIP()
        when(level){
            4 -> {
                serverNetWorkStatus.text = "网络极好"
                serverNetWorkStatus.setTextColor(resources.getColor(R.color.f_server_manager_network_online))
            }
            3-> {
                serverNetWorkStatus.text = "网络优"
                serverNetWorkStatus.setTextColor(resources.getColor(R.color.f_server_manager_network_online))
            }
            2->{
                serverNetWorkStatus.text = "网络良"
                serverNetWorkStatus.setTextColor(resources.getColor(R.color.f_server_manager_network_online))
            }
            1->{
                serverNetWorkStatus.text = "网络差"
                serverNetWorkStatus.setTextColor(resources.getColor(R.color.f_server_manager_network_online))
            }
            0->{
                serverNetWorkStatus.text = "网络已断开"
                serverNetWorkStatus.setTextColor(resources.getColor(R.color.f_server_manager_network_offline))
                activity!!.stopService(serverIntent)
            }
        }
    }

    fun onServerStarted(){
        btnServerOption.text = "关闭服务"
        btnServerOption.background = btnStopSelector
        refreshServerStatus()
    }

    fun onServerStopped(){
        btnServerOption.text = "启动服务"
        btnServerOption.background = btnStartSelector
        refreshServerStatus()
    }

    private fun refreshServerStatus(){
        var server = LanShare.getServer()
        logWarn(server.isRunning.toString())
        when(server.isRunning){
            true -> {
                serverRunningHost.text = server.host
                serverRunningHost.setTextColor(resources.getColor(R.color.f_server_manager_status_ok))
                serverRunningPort.text = server.port.toString()
                serverRunningPort.setTextColor(resources.getColor(R.color.f_server_manager_status_ok))
                serverRunningTimeout.text = server.timeout.toString()
                serverRunningTimeout.setTextColor(resources.getColor(R.color.f_server_manager_status_ok))
                serverRunningStatus.text = getText(R.string.fragment_server_manager_server_running)
                serverRunningStatus.setTextColor(resources.getColor(R.color.f_server_manager_status_ok))
                serverRunningTime.text = DateUtil.getDateAndTime(System.currentTimeMillis())
            }
            false ->{
                serverRunningHost.text = getString(R.string.fragment_server_manager_server_not_run)
                serverRunningHost.setTextColor(resources.getColor(R.color.f_server_manager_status_warn))
                serverRunningPort.text = getString(R.string.fragment_server_manager_server_not_run)
                serverRunningPort.setTextColor(resources.getColor(R.color.f_server_manager_status_warn))
                serverRunningTimeout.text = getString(R.string.fragment_server_manager_server_not_run)
                serverRunningTimeout.setTextColor(resources.getColor(R.color.f_server_manager_status_warn))
                serverRunningStatus.text = getString(R.string.fragment_server_manager_server_not_run)
                serverRunningStatus.setTextColor(resources.getColor(R.color.f_server_manager_status_warn))
                serverRunningTime.text = "0"
            }
        }
    }

    fun setupViews(view:View){

    }

    companion object {
        private const val TAG = "ServerManagerFragment"
    }
}