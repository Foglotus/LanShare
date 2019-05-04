package com.foglotus.main.common.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.foglotus.core.util.GlobalUtil
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.main.common.listener.PermissionListener
import kotlinx.android.synthetic.main.activity_need_permission.*

/**
 *
 * @author foglotus
 * @since 2019/2/27
 */
class PermissionActivity: BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_need_permission)
        agree.setOnClickListener {
            refreshPermission()
        }
    }

    private fun refreshPermission(){
        handlePermissions(permissionList,object: PermissionListener {
            override fun onGranted() {
                if (model) {
                    finish()
                } else {
                    MainActivity.actionStart(activity!!)
                    finish()
                }
            }

            override fun onDenied(deniedPermissions: List<String>) {
                var allNeverAskAgain = true
                for (deniedPermission in deniedPermissions) {
                    if (shouldShowRequestPermissionRationale(deniedPermission)) {
                        allNeverAskAgain = false
                        break
                    }
                }
                // 所有的权限都被勾上不再询问时，跳转到应用设置界面，引导用户手动打开权限
                if (allNeverAskAgain) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", GlobalUtil.appPackage, null)
                    intent.data = uri
                    activity!!.startActivityForResult(intent, 1016)
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1016){
            refreshPermission()
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    companion object {
        private const val TAG = "PermissionActivity"
        private var model = false
        fun actionStart(activity: Activity) {
            val intent = Intent(activity, PermissionActivity::class.java)
            activity.startActivity(intent)
        }
        fun actionStartWithBack(activity: Activity){
            model = true
            val intent = Intent(activity, PermissionActivity::class.java)
            activity.startActivity(intent)
        }
    }
}