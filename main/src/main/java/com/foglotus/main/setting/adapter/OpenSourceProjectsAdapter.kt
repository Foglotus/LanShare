package com.foglotus.main.setting.adapter
import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.foglotus.main.R
import com.foglotus.main.base.activity.WebViewActivity
import com.foglotus.main.setting.model.OpenSourceProject

/**
 * 开源项目列表的RecyclerView适配器。
 * @author guolin
 * @since 2018/6/29
 */
class OpenSourceProjectsAdapter(val activity: Activity, private val projectList: List<OpenSourceProject>) : RecyclerView.Adapter<OpenSourceProjectsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.open_source_project_item, parent, false)
        val holder = ViewHolder(view)
        holder.rootLayout.setOnClickListener {
            val position = holder.adapterPosition
            val project = projectList[position]
            WebViewActivity.actionStart(activity, project.name, project.url)
        }
        return holder
    }

    override fun getItemCount(): Int = projectList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = projectList[position]
        holder.name.text = item.name
        holder.url.text = item.url
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var rootLayout: FrameLayout = view.findViewById(R.id.rootLayout)

        var name: TextView = view.findViewById(R.id.name)

        var url: TextView = view.findViewById(R.id.url)

    }

}