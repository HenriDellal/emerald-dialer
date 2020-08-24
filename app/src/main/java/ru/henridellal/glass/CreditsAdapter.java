package ru.henridellal.glass;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.henridellal.dialer.R;

public class CreditsAdapter extends ArrayAdapter<CreditsEntry> {

	private int projectsSectionTitleIndex;

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SECTION_TITLE = 1;

	public CreditsAdapter(Context context, int resource, ArrayList<CreditsEntry> contributors, ArrayList<CreditsEntry> projects) {
		super(context, resource);
		ArrayList<CreditsEntry> objects = new ArrayList<CreditsEntry>();
		if (null != contributors && contributors.size() > 0) {
			CreditsEntry contributorsTitle = new CreditsEntry();
			contributorsTitle.setName(context.getResources().getString(R.string.glass_section_contributors));
			objects.add(contributorsTitle);
			objects.addAll(contributors);
		}
		if (null != projects && projects.size() > 0) {
			CreditsEntry projectsTitle = new CreditsEntry();
			projectsTitle.setName(context.getResources().getString(R.string.glass_section_projects));
			projectsSectionTitleIndex = objects.size();
			objects.add(projectsTitle);
			objects.addAll(projects);
		}
		addAll(objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		int viewType = getItemViewType(position);
		CreditsEntry entry = ((CreditsEntry)getItem(position));

		if (null == convertView) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch(viewType) {
				case TYPE_SECTION_TITLE:
					view = inflater.inflate(R.layout.glass_section_title, parent, false);
					break;
				default:
					view = inflater.inflate(R.layout.glass_credits_item, parent, false);
			}
		} else {
			view = convertView;
		}

		view.setTag(entry.getUrl());
		switch(viewType) {
			case TYPE_SECTION_TITLE:
				((TextView)view.findViewById(R.id.glass_section_title)).setText(entry.getName());
				break;
			case TYPE_ITEM:
				((TextView)view.findViewById(R.id.glass_item_name)).setText(entry.getName());
				((TextView)view.findViewById(R.id.glass_item_info)).setText(entry.getInfo());
		}

		return view;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0 || position == projectsSectionTitleIndex) {
			return TYPE_SECTION_TITLE;
		} else {
			return TYPE_ITEM;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

}
