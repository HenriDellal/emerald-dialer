package ru.henridellal.glass;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.henridellal.dialer.R;

public class GlassActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

	private CreditsAdapter adapter;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.glass_main);

		ArrayList<CreditsEntry> contributors = getCredits(
			R.array.glass_contributors_names,
			R.array.glass_contributors_info,
			R.array.glass_contributors_urls);

		ArrayList<CreditsEntry> projects = getCredits(
			R.array.glass_projects_names,
			R.array.glass_projects_info,
			R.array.glass_projects_urls);

		adapter = new CreditsAdapter(this, R.layout.glass_credits_item, contributors, projects);
		ListView creditsView = (ListView)findViewById(R.id.glass_credits); 
		creditsView.setOnItemClickListener(this);
		View header = LayoutInflater.from(this).inflate(R.layout.glass_main_header, null);
		creditsView.addHeaderView(header);
		creditsView.setAdapter(adapter);

		String aboutText = String.format(getResources().getString(R.string.glass_text), getResources().getString(R.string.glass_app_version));
		((TextView)findViewById(R.id.glass_text)).setText(aboutText);

		setLinkVisibility(R.id.glass_btn_feedback, R.string.glass_feedback_url);
		setLinkVisibility(R.id.glass_btn_source_code, R.string.glass_source_code_url);
		setLinkVisibility(R.id.glass_btn_support, R.string.glass_support_url);
	}

	private ArrayList<CreditsEntry> getCredits(int namesArrayId, int infoArrayId, int urlArrayId) {
		Resources res = getResources();
		ArrayList<CreditsEntry> credits = new ArrayList<CreditsEntry>();
		String[] names = res.getStringArray(namesArrayId);
		String[] info = res.getStringArray(infoArrayId);
		String[] urls = res.getStringArray(urlArrayId);
		for (int i = 0; i < names.length; i++) {
			CreditsEntry entry = new CreditsEntry();
			entry.setName(names[i]);
			entry.setInfo(info[i]);
			entry.setUrl(urls[i]);
			credits.add(entry);
		}

		return credits;
	}

	private void setLinkVisibility(int viewId, int linkId) {
		String link = getResources().getString(linkId);
		View view = findViewById(viewId);
		if (link.length() > 0) {
			view.setTag(link);
			view.setOnClickListener(this);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		openLink((String)view.getTag());
	}

	private void openLink(String link) {
		Uri uri = Uri.parse(link);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(intent);
		} catch (Exception e) {

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String link = (String)view.getTag();
		if (null != link) {
			openLink(link);
		}
	}
}
