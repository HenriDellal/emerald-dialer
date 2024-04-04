package ru.henridellal.dialer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class CustomSpinnerAdapter extends ArrayAdapter<DateDiff> implements SpinnerAdapter {
	public CustomSpinnerAdapter(Context context, DateDiff[] objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setTag(getItem(position));

		return view;
	}
}