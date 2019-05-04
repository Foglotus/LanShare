package com.foglotus.main.setting.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.main.common.view.SimpleDividerDecoration
import com.foglotus.main.setting.adapter.OpenSourceProjectsAdapter
import com.foglotus.main.setting.model.OpenSourceProject
import kotlinx.android.synthetic.main.activity_open_source_projects.*

class OpenSourceProjectsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_source_projects)
    }

    override fun setupViews() {
        setupToolbar()
        val layoutManager = LinearLayoutManager(this)
        val adapter = OpenSourceProjectsAdapter(this, getProjectList())
        openSourceRecyclerView.adapter = adapter
        openSourceRecyclerView.layoutManager = layoutManager
        openSourceRecyclerView.addItemDecoration(SimpleDividerDecoration(this))
    }

    private fun getProjectList() = ArrayList<OpenSourceProject>().apply {
        add(OpenSourceProject("GifFun","https://github.com/guolindev/giffun"))
        add(OpenSourceProject("NanoHttpd","https://github.com/NanoHttpd/nanohttpd"))
        add(OpenSourceProject("H-admin","http://www.h-ui.net/index.shtml"))
        add(OpenSourceProject("W3Layout","https://w3layouts.com/"))
        add(OpenSourceProject("Glide", "https://github.com/bumptech/glide"))
        add(OpenSourceProject("OkHttp", "https://github.com/square/okhttp"))
        add(OpenSourceProject("Gson", "https://github.com/google/gson"))
        add(OpenSourceProject("EventBus", "https://github.com/greenrobot/EventBus"))
        add(OpenSourceProject("LitePal", "https://github.com/LitePalFramework/LitePal"))
    }

    companion object {

        const val TAG = "OpenSourceProjectsActivity"

        fun actionStart(context: Context) {
            val intent = Intent(context, OpenSourceProjectsActivity::class.java)
            context.startActivity(intent)
        }

    }

}