package ru.henridellal.dialer;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NumpadButton extends RelativeLayout {
	
	private String mChar;
	private String mLetters, mLocalLetters;
	
	public NumpadButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initButton(attrs);
	}
	
	public NumpadButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initButton(attrs);
	}
	
	public NumpadButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initButton(attrs);
	}
	
	private void initButton(AttributeSet attrs) {
		LayoutInflater inflater;
		if (getContext() instanceof Activity) {
			inflater = ((Activity)getContext()).getLayoutInflater();
		} else {
			inflater = LayoutInflater.from(getContext());
		}
		
		inflater.inflate(R.layout.numpad_button, this, true);
		TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.NumpadButton, 0, 0);
		try {
			mChar = arr.getString(R.styleable.NumpadButton_number);
			mLetters = arr.getString(R.styleable.NumpadButton_letters);
			mLocalLetters = arr.getString(R.styleable.NumpadButton_localLetters);
		} finally {
			arr.recycle();
		}
		((TextView)findViewById(R.id.numpad_number)).setText(mChar);
		((TextView)findViewById(R.id.numpad_letters)).setText(mLetters);
		setLetters(mLocalLetters);
	}

	public void setLetters(String letters) {
		mLocalLetters = letters;
		TextView numpadLettersLocalView = (TextView) findViewById(R.id.numpad_letters_local);
		if (null == mLocalLetters || mLocalLetters.isEmpty()) {
			numpadLettersLocalView.setVisibility(View.GONE);
		} else {
			numpadLettersLocalView.setVisibility(View.VISIBLE);
			numpadLettersLocalView.setText(mLocalLetters);
		}
	}
}
