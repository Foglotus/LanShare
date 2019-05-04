package com.foglotus.lanshare

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.foglotus.main.setting.activity.AboutActivity

/**
 *
 * @author foglotus
 * @since 2019/2/22
 */
class LanShareAboutActivity : AboutActivity() {
    override fun setupViews() {
        super.setupViews()
//        button.text = GlobalUtil.getString(R.string.download_official)
//        button.setOnClickListener {
//            val uri = Uri.parse("market://details?id=club.giffun.app")
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = uri
//            val packageManager = GifFun.getContext().packageManager
//            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//            if (list.size > 0) {
//                startActivity(intent)
//            } else {
//                showToast(GlobalUtil.getString(R.string.unable_goto_app_store), Toast.LENGTH_LONG)
//            }
//        }
        //showToast("在这里我可以慢慢的查找更新")
    }

    companion object {

        const val TAG = "LanShareAboutActivity"

    }
}