package ru.henridellal.glass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.henridellal.dialer.R
import java.util.*

class CreditsAdapter(context: Context, resource: Int, contributors: ArrayList<CreditsEntry>?, projects: ArrayList<CreditsEntry>?) : ArrayAdapter<CreditsEntry?>(context, resource) {
    private var projectsSectionTitleIndex = 0
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewType = getItemViewType(position)
        val entry = getItem(position)
        view = if (null == convertView) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            when (viewType) {
                TYPE_SECTION_TITLE -> inflater.inflate(R.layout.glass_section_title, parent, false)
                else -> inflater.inflate(R.layout.glass_credits_item, parent, false)
            }
        } else {
            convertView
        }
        view.tag = entry!!.url
        when (viewType) {
            TYPE_SECTION_TITLE -> (view.findViewById<View>(R.id.glass_section_title) as TextView).text = entry.name
            TYPE_ITEM -> {
                (view.findViewById<View>(R.id.glass_item_name) as TextView).text = entry.name
                (view.findViewById<View>(R.id.glass_item_info) as TextView).text = entry.info
            }
        }
        return view
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == projectsSectionTitleIndex) {
            TYPE_SECTION_TITLE
        } else {
            TYPE_ITEM
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_SECTION_TITLE = 1
    }

    init {
        val objects = ArrayList<CreditsEntry>()
        if (null != contributors && contributors.size > 0) {
            val contributorsTitle = CreditsEntry()
            contributorsTitle.name = context.resources.getString(R.string.glass_section_contributors)
            objects.add(contributorsTitle)
            objects.addAll(contributors)
        }
        if (null != projects && projects.size > 0) {
            val projectsTitle = CreditsEntry()
            projectsTitle.name = context.resources.getString(R.string.glass_section_projects)
            projectsSectionTitleIndex = objects.size
            objects.add(projectsTitle)
            objects.addAll(projects)
        }
        addAll(objects)
    }
}