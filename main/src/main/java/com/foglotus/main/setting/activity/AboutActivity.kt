package com.foglotus.main.setting.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Button
import com.foglotus.core.LanShare
import com.foglotus.core.util.GlobalUtil
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*

open class AboutActivity : BaseActivity() {

    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    override fun setupViews() {
        setupToolbar()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        button = checkUpdateButton
        val version = "${GlobalUtil.getString(R.string.version)} ${GlobalUtil.appVersionName}"
        aboutVersion.text = version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            openSourceListTextView.text = Html.fromHtml("<u>"+ GlobalUtil.getString(R.string.about_open_source_list) +"</u>", 0)
        } else {
            openSourceListTextView.text = Html.fromHtml("<u>"+ GlobalUtil.getString(R.string.about_open_source_list) +"</u>")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            privateSourceListTextView.text = Html.fromHtml("<u>"+ GlobalUtil.getString(R.string.about_private_source_list) +"</u>", 0)
        } else {
            privateSourceListTextView.text = Html.fromHtml("<u>"+ GlobalUtil.getString(R.string.about_private_source_list) +"</u>")
        }
        aboutLogo.setImageDrawable(GlobalUtil.getAppIcon())
        openSourceListTextView.setOnClickListener {
            OpenSourceProjectsActivity.actionStart(this)
        }
        privateSourceListTextView.setOnClickListener {
            PrivateSourceProjectsActivity.actionStart(this)
        }
    }

    companion object {

        const val TAG = "AboutActivity"

        private val ACTION_VIEW_ABOUT = "${LanShare.getPackageName()}.ACTION_VIEW_ABOUT"

        fun actionStart(context: Context) {
            context.startActivity(Intent(ACTION_VIEW_ABOUT))
        }
    }
}
