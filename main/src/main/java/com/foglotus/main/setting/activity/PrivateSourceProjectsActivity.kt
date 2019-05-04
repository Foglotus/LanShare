package com.foglotus.main.setting.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.main.common.view.SimpleDividerDecoration
import com.foglotus.main.setting.adapter.PrivateSourceProjectsAdapter
import com.foglotus.main.setting.model.PrivateSourceProject
import kotlinx.android.synthetic.main.activity_private_source_projects.*


class PrivateSourceProjectsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_source_projects)
    }

    override fun setupViews() {
        setupToolbar()
        val layoutManager = LinearLayoutManager(this)
        val adapter = PrivateSourceProjectsAdapter(this, getProjectList())
        privateSourceRecyclerView.adapter = adapter
        privateSourceRecyclerView.layoutManager = layoutManager
        privateSourceRecyclerView.addItemDecoration(SimpleDividerDecoration(this))
    }

    private fun getProjectList() = ArrayList<PrivateSourceProject>().apply {
        add(PrivateSourceProject("扇贝单词","使用了APP内的图标，开源列表页面也稍微有点借鉴,很推荐这款单词APP，已经用了四年了，六级还没过","https://www.shanbay.com/"))
        add(PrivateSourceProject("GiFan","整个项目的架构都模仿、借鉴、采用了郭霖先生的趣闲APP开源作品，没有趣闲就没有LanShare","https://github.com/guolindev/giffun"))
        add(PrivateSourceProject("AndServer","参考了架构设计，但最终模仿了1%，太高端了，如果直接用了就没有我发挥的空间了。。。","https://github.com/yanzhenjie/AndServer"))
        add(PrivateSourceProject("白描","你会发现资源列表页面的好像白描扫描历史记录页面的样式，没错！灵感和创作皆来自于此","https://baimiao.uzero.cn/"))
        add(PrivateSourceProject("WebUploader","直接使用了Demo中的上传进度的css样式，我自己写的百分比总是会被进度背景挤出去","http://fex.baidu.com/webuploader/demo.html"))
        add(PrivateSourceProject("上传页面上传JS核心代码","使用了这位博主的代码，并加以改造融入我自己的皮毛,没有这位提供的代码，我现在还不会上传","https://blog.csdn.net/andywei147/article/details/80636539"))
        add(PrivateSourceProject("RecyclerView实现滑动删除和拖拽功能","使用了这位博主的代码","https://www.jianshu.com/p/2ae483118c8e"))
        add(PrivateSourceProject("局域网精灵","借鉴了创作灵感，使用过这款APP之后，产生了做毕业设计的想法","https://www.coolapk.com/apk/com.xchat.stevenzack.langenius"))
        add(PrivateSourceProject("APP图标","你可能觉得本APP的小图标很好看，这只小鹦鹉具体是谁的，我不知道，我是从网上找的","https://127.0.0.1"))
    }

    companion object {

        const val TAG = "PrivateSourceProjectsActivity"

        fun actionStart(context: Context) {
            val intent = Intent(context, PrivateSourceProjectsActivity::class.java)
            context.startActivity(intent)
        }

    }

}