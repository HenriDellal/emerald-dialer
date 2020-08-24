package ru.henridellal.glass

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import ru.henridellal.dialer.R
import java.util.*

open class GlassActivity : Activity(), View.OnClickListener, OnItemClickListener {
    private var adapter: CreditsAdapter? = null
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.glass_main)
        val contributors = getCredits(
                R.array.glass_contributors_names,
                R.array.glass_contributors_info,
                R.array.glass_contributors_urls)
        val projects = getCredits(
                R.array.glass_projects_names,
                R.array.glass_projects_info,
                R.array.glass_projects_urls)
        adapter = CreditsAdapter(this, R.layout.glass_credits_item, contributors, projects)
        val creditsView = findViewById<View>(R.id.glass_credits) as ListView
        creditsView.onItemClickListener = this
        val header = LayoutInflater.from(this).inflate(R.layout.glass_main_header, null)
        creditsView.addHeaderView(header)
        creditsView.adapter = adapter
        val aboutText = String.format(resources.getString(R.string.glass_text), resources.getString(R.string.glass_app_version))
        (findViewById<View>(R.id.glass_text) as TextView).text = aboutText
        setLinkVisibility(R.id.glass_btn_feedback, R.string.glass_feedback_url)
        setLinkVisibility(R.id.glass_btn_source_code, R.string.glass_source_code_url)
        setLinkVisibility(R.id.glass_btn_support, R.string.glass_support_url)
    }

    private fun getCredits(namesArrayId: Int, infoArrayId: Int, urlArrayId: Int): ArrayList<CreditsEntry> {
        val res = resources
        val credits = ArrayList<CreditsEntry>()
        val names = res.getStringArray(namesArrayId)
        val info = res.getStringArray(infoArrayId)
        val urls = res.getStringArray(urlArrayId)
        for (i in names.indices) {
            val entry = CreditsEntry()
            entry.name = names[i]
            entry.info = info[i]
            entry.url = urls[i]
            credits.add(entry)
        }
        return credits
    }

    private fun setLinkVisibility(viewId: Int, linkId: Int) {
        val link = resources.getString(linkId)
        val view = findViewById<View>(viewId)
        if (link.isNotEmpty()) {
            view.tag = link
            view.setOnClickListener(this)
        } else {
            view.visibility = View.GONE
        }
    }

    override fun onClick(view: View) {
        openLink(view.tag as String)
    }

    private fun openLink(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val link = view.tag as String
        openLink(link)
    }
}