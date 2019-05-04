package com.foglotus.main.web.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foglotus.core.Const
import com.foglotus.core.LanShare
import com.foglotus.core.dialog.SweetAlertDialog
import com.foglotus.core.util.FileUtil
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.util.SharedUtil
import com.foglotus.core.extention.logInfo
import com.foglotus.lanshare.core.extension.showToast
import com.foglotus.lanshare.core.extension.showToastOnUiThread
import com.foglotus.main.R
import com.foglotus.main.base.callback.OnRecyclerItemClickListener
import com.foglotus.main.web.view.DownloadDialog
import com.foglotus.main.common.fragment.DataFragment
import com.foglotus.main.common.view.SimpleDividerDecoration
import com.foglotus.main.download.activity.DownloadActivity
import com.foglotus.main.download.event.AddDownloadEvent
import com.foglotus.main.home.util.ServerUtil
import com.foglotus.main.web.adapter.FileAdapter
import com.foglotus.main.web.view.MultiFloatingActionButton
import com.foglotus.main.web.view.TagFabLayout
import com.foglotus.main.web.view.UploadDialog
import com.foglotus.network.model.*
import com.foglotus.network.service.DownloadService
import com.foglotus.network.util.UploadUtil
import com.leon.lfilepickerlibrary.LFilePicker
import kotlinx.android.synthetic.main.f_web_download_add.*
import kotlinx.android.synthetic.main.fragment_web_layout.*
import kotlinx.android.synthetic.main.menu_floating_btn.*
import org.greenrobot.eventbus.EventBus
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

/**
 *
 * @author foglotus
 * @since 2019/3/17
 */
class WebFragment:DataFragment(){

    private val files:MutableList<File> = ArrayList()
    private lateinit var adapter: FileAdapter

    private var backData:ArrayList<MutableList<File>> = ArrayList()
    private var backUrl:ArrayList<String> = ArrayList()

    private var path:String = ""

    private val downloadIntent by lazy {
        Intent(LanShare.getContext(),DownloadService::class.java)
    }

    private val runnable = object :Runnable{
        override fun run() {
            updateColor(Random(System.currentTimeMillis()).nextInt(7))
            LanShare.getHandler().postDelayed(this,500)
        }
    }
    var dialog: SweetAlertDialog?=null
    var downloadDialog:DownloadDialog ?= null
    var uploadDialog:UploadDialog ?= null

    private var host:String = ""
    private var uuid:String = SharedUtil.read("uuid","")

    private lateinit var mulFloActBtn: MultiFloatingActionButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_web_layout,container,false)
        mulFloActBtn = view.findViewById(R.id.multiFloatBtn)
        mulFloActBtn.setOnFabItemClickListener(object:MultiFloatingActionButton.OnFabItemClickListener{
            override fun onFabItemClick(view: TagFabLayout?, pos: Int) {
                when(view){
                    downloadManage -> {
                        showToast("点击了下载管理")
                        DownloadActivity.actionStart(activity as Activity)
                    }
                    upload ->{
                        if(host.isEmpty()){
                            dialog = SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE)
                            dialog?.titleText = "尚未登录"
                            dialog?.contentText = "请点击打开，完成登录"
                            dialog?.setConfirmClickListener {
                                dialog?.dismiss()
                            }
                            dialog?.show()
                            dialog?.showConfirmButton(true)
                        }else{
                            uploadDialog = UploadDialog(activity)
                            uploadDialog?.setCancelClickListener {
                                uploadDialog?.dismiss()
                            }
                            uploadDialog?.setFolderClickListener {
                                LFilePicker()
                                    .withSupportFragment(this@WebFragment)
                                    .withRequestCode(FILE_UPLOAD_PATH)
                                    .withMutilyMode(false)
                                    .withChooseMode(true)
                                    .start()
                            }
                            uploadDialog?.setConfirmClickListener {
                                val path = uploadDialog?.uploadPath
                                if(!path.isNullOrEmpty()){
                                    uploadDialog?.showConfirmButton(false)
                                    UploadUtil.get().upload(host,path,uuid,object:UploadUtil.OnUploadListener{
                                        override fun onSuccess() {
                                            activity?.runOnUiThread {
                                                uploadDialog?.showConfirmButton(true)
                                                uploadDialog?.setError("上传成功",false)
                                                uploadDialog?.setProgress(100)
                                            }
                                        }

                                        override fun onFailed(message: String) {
                                            activity?.runOnUiThread {
                                                uploadDialog?.showConfirmButton(true)
                                                uploadDialog?.setError(message,true)
                                            }
                                        }

                                        override fun onUploading(progress: Int) {
                                            activity?.runOnUiThread {
                                                uploadDialog?.setProgress(progress)
                                            }
                                        }

                                    })
                                }else{
                                    showToastOnUiThread("尚未选择文件")
                                }
                            }
                            uploadDialog?.show()
                        }
                    }
                    openLink ->{
                        showToast("点击了打开链接")
                        dialog = SweetAlertDialog(activity,SweetAlertDialog.OPEN_TYPE)
                        dialog?.titleText = "输入主机地址"
                        dialog?.setConfirmClickListener {
                            if(!dialog?.host.isNullOrEmpty()){
                                host = dialog?.host!!
                                dialog?.titleText = "尝试连接"
                                dialog?.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
                                Check.getResponse(object :Callback{
                                    override fun onResponse(response: Response) {
                                        if(response.status == Const.ServerResponse.CHECK_SUCCESS){
                                            dialog?.titleText = response.msg
                                            dialog?.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                            LanShare.getHandler().postDelayed(Runnable { showCheckPrivilege()},1000)
                                        }
                                    }

                                    override fun onFailure(e: Exception) {
                                        host = ""
                                        e.printStackTrace()
                                        LanShare.getHandler().postDelayed(Runnable {
                                            dialog?.titleText = e.message
                                            dialog?.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                            LanShare.getHandler().postDelayed(Runnable {
                                                dialog?.dismissWithAnimation()
                                            },2000)
                                        },1500)
                                    }

                                },host)
                            }else{
                                showToast("请输入IP地址以及端口号")
                            }
                        }
                        dialog?.show()
                    }
                }
            }

        })
        return super.onCreateView(view)
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

    private fun showCheckPrivilege(){
        dialog?.titleText = "权限认证"
        dialog?.changeAlertType(SweetAlertDialog.PRIVILEGE_TYPE)
        dialog?.setConfirmClickListener {
            val code = dialog?.code
            if (code != null){
                checkPrivilege(code)
            }
        }
    }

    private fun checkPrivilege(code:String){
        dialog?.titleText = "正在验证"
        dialog?.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
        Privilege.getResponse(object:Callback{
            override fun onResponse(response: Response) {
                if(response.status == Const.ServerResponse.CHECK_SUCCESS){
                    uuid = (response as Privilege).uuid
                    logInfo("服务器传输过来的uuid:$uuid")
                    dialog?.titleText = response.msg
                    dialog?.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    SharedUtil.save("uuid",uuid)
                    LanShare.getHandler().postDelayed(
                        Runnable {
                            dialog?.titleText = "正在获取数据"
                            dialog?.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
                            loadData()

                        }
                        ,1000)

                }else{
                    dialog?.titleText = response.msg
                    dialog?.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    LanShare.getHandler().postDelayed(Runnable {
                        showCheckPrivilege()
                    },1000)
                }
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                dialog?.titleText = e.message
                dialog?.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                LanShare.getHandler().postDelayed(Runnable {
                    dialog?.dismissWithAnimation()
                },1000)
            }


        },host,uuid,code)
    }

    private fun getData(){
        FileList.getResponse(object :Callback{
            override fun onResponse(response: Response) {
                logInfo("返回消息"+response.msg)


            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                LanShare.getHandler().postDelayed(
                    Runnable {
                        dialog?.dismissWithAnimation()
                    },1000)
            }

        },host,uuid,"")
    }

    override fun setRecyclerViews() {
        layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
        adapter = FileAdapter(this,files)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(SimpleDividerDecoration(LanShare.getContext()))
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView.addOnItemTouchListener(object :OnRecyclerItemClickListener(recyclerView){
            override fun onItemClick(vh: RecyclerView.ViewHolder?) {
                if(vh is FileAdapter.FileViewHolder){
                    val position = vh.adapterPosition
                    val file = files[position]
                    if(file.type == "文件夹"){
                        handleClick(file)
                    }else{
                        downloadDialog = DownloadDialog(activity)
                        downloadDialog?.show()
                        logInfo(file.toString())
                        downloadDialog?.downloadName = file.name
                        downloadDialog?.downloadPath = GlobalUtil.getDefaultDownloadPath()
                        downloadDialog?.downloadUrl = "http://"+host+file.path

                        downloadDialog?.setCancelClickListener {
                            logInfo("你点击了取消")
                            downloadDialog?.dismiss()
                        }

                        downloadDialog?.setConfirmClickListener {
                            downloadDialog?.confirm_button?.isEnabled = false

                            val url = downloadDialog?.downloadUrl
                            val name = downloadDialog?.downloadName
                            val path = downloadDialog?.downloadPath

                            if(url.isNullOrEmpty() || name.isNullOrEmpty() || path.isNullOrEmpty()){
                                showToastOnUiThread("未选择下载文件，请重新选择")
                            }else{
                                val newFile = DownloadFile(name,file.type,Date(),path,0,FileUtil.getConvertSize(file.size.toLong()),DownloadFile.DOWNLOADING,url)
                                newFile.save()
                                logInfo("存储的文件的id是$id")
                                downloadIntent.putExtra("id",newFile.id)
                                downloadIntent.putExtra("path",path)
                                downloadIntent.putExtra("name",name)
                                downloadIntent.putExtra("url",url)
                                EventBus.getDefault().post(AddDownloadEvent(newFile))
                                activity!!.startService(downloadIntent)
                                LanShare.getHandler().postDelayed(Runnable {
                                    downloadDialog?.dismiss()
                                },1000)
                                downloadDialog?.confirm_button!!.isEnabled = true
                            }
                        }
                        downloadDialog?.setFolderClickListener {
                            LFilePicker()
                                .withSupportFragment(this@WebFragment)
                                .withRequestCode(FILE_DOWNLOAD_SAVE_PATH)
                                .withStartPath(ServerUtil.getServerDefaultUploadPath())
                                .withChooseMode(false)
                                .start()
                        }
                    }
                }
            }

            override fun onItemLongClick(vh: RecyclerView.ViewHolder?) {

            }

        })
        swipeRefresh.setOnRefreshListener { refreshFeeds() }

        showNoContentView("未连接到远程服务")
        back.setOnClickListener {
            onLoading()
            if(backUrl.size > 0){
                path = backUrl.removeAt(0)
                handleData(backData.removeAt(0))
            }
        }
    }

    override fun refreshFeeds() {
        loadData()
    }

    private fun handleData(data:MutableList<File>){
        files.clear()
        files.addAll(data)
        if(files.size > 0){
            activity?.runOnUiThread {
                recyclerView.visibility = View.VISIBLE
                hideNoContentView()
                adapter.notifyDataSetChanged()
            }
        }else{
            recyclerView.visibility = View.GONE
            showNoContentView("没有文件")
        }
        loadFinished()
    }

    private fun clearStack(){
        backData.clear()
        backUrl.clear()
    }

    override fun loadData() {
        FileList.getResponse(object :Callback{
            override fun onResponse(response: Response) {
                if(response.status != Const.ServerResponse.SUCCESS){
                    dialog?.titleText = response.msg
                    logInfo("走这里了")
                    dialog?.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog?.cancelText = "取消"
                    dialog?.confirmText = "确定"
                    dialog?.contentText = "是否重试？"
                    dialog?.showContentText(true)
                    dialog?.showCancelButton(true)
                    dialog?.showConfirmButton(true)
                    dialog?.setCancelClickListener {
                        dialog?.dismissWithAnimation()
                    }
                    dialog?.setConfirmClickListener {
                        dialog?.titleText = "加载中"
                        dialog?.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
                        loadData()
                    }
                }else{
                    path = response.msg.split("|")[1]
                    handleData((response as FileList).data)
                }
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
            }

        },host,uuid,path)
    }

    override fun loadFromDB(isLoadMore: Boolean, lastID: Long) {

    }

    override fun onLoading() {
        dialog = SweetAlertDialog(activity,SweetAlertDialog.PROGRESS_TYPE)
        dialog?.titleText = "加载中"
        dialog?.show()
    }

    override fun loadFinished() {
        super.loadFinished()
        swipeRefresh.isRefreshing = false
        dialog?.dismiss()
        if(backData.size >0){
            back.visibility = View.VISIBLE
        }else{
            back.visibility = View.GONE
        }
    }

    fun handleClick(item: File) {
        onLoading()
        backUrl.add(0,path)
        val file:MutableList<File> = ArrayList()
        file.addAll(files.drop(0))
        logInfo("移除获得"+file.size.toString())
        logInfo("移除之后"+files.size.toString())
        backData.add(0,file)
        path = URLEncoder.encode(item.path,"UTF-8")
        loadData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logInfo("WebFragment",requestCode.toString())
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FILE_DOWNLOAD_SAVE_PATH ->{
                    val path = data?.getStringExtra("path")
                    if(path != null && !TextUtils.isEmpty(path)){
                        handleSelectDownloadPath(path)
                    }
                }
                FILE_UPLOAD_PATH ->{
                    val list = data?.getStringArrayListExtra("paths")
                    if(list != null && list.size > 0 && !TextUtils.isEmpty(list[0])){
                        handleSelectUploadPath(list[0])
                    }
                }
            }
        }
    }

    private fun handleSelectDownloadPath(path: String) {
        logInfo("收到返回消息$path")
        downloadDialog?.downloadPath = path
    }

    private fun handleSelectUploadPath(path:String){
        uploadDialog?.uploadPath = path
    }

    companion object{
        const val FILE_DOWNLOAD_SAVE_PATH = 2000
        const val FILE_UPLOAD_PATH = 2001
    }
}