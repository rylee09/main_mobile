package com.example.st.arcgiscss.activites;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.d3View.D3Activity;
import com.example.st.arcgiscss.util.HideSoftInputHelperTool;
import com.example.st.arcgiscss.util.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseActivity extends D3Activity {

	public ImageView leftButton, rightButton;
	TextView titleTextView;
	FrameLayout titleFrameLayout;
	
	public void initTitle(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// Vertical screen
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		titleTextView = (TextView) findViewById(R.id.titleView);
		titleFrameLayout=(FrameLayout)findViewById(R.id.titlebar);
//		leftButton = (ImageView) findViewById(R.id.leftBtn);
//		rightButton = (ImageView) findViewById(R.id.rightBtn);
		if(leftButton!=null)
			leftButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
	}

	protected void setTitleVisibility(boolean isVisibility) {
		if (isVisibility) {
			titleFrameLayout.setVisibility(View.VISIBLE);
		} else {
			titleFrameLayout.setVisibility(View.GONE);
		}

	}

	// If the left button is set to false to hide
	protected void setLeftButtonVisibility(boolean isVisibility) {
		if (isVisibility) {
			leftButton.setVisibility(View.VISIBLE);
		} else {
			leftButton.setVisibility(View.GONE);
		}

	}


	/** If the right button is set to false to hide */
	protected void setRightButtonVisibility(boolean isVisibility) {
		if (isVisibility) {
			rightButton.setVisibility(View.VISIBLE);
		} else {
			rightButton.setVisibility(View.GONE);
		}
	}

//	protected void setLeftButtonText(int id) {
//		leftButton.setText(getString(id));
//	}
//
//	protected void setRightButtonText(int id) {
//		rightButton.setText(getString(id));
//	}
//	protected void setRightButtonText(String id) {
//		rightButton.setText(id);
//	}
	
	protected void setTitleString(String title) {
		if (titleTextView!=null) {
			titleTextView.setText(title);
		}
	}

	protected void setRightButtonDrawable(int drawable) {
		rightButton.setBackgroundResource(drawable);
	}


	protected void editTextClean(final EditText ed, final ImageView deteleImageView) {
		ed.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					deteleImageView.setVisibility(View.GONE);
				} else {
					deteleImageView.setVisibility(View.VISIBLE);
					deteleImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							ed.setText("");

						}

					});
				}

			}

		});
	}

	// Keyboard processing Click on the blank page to close the keyboard
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		HideSoftInputHelperTool.hide(this, ev);
		return super.dispatchTouchEvent(ev);
	}

	private String getMatcherResource(String activityName) {
		String resourceName;
		if (activityName.contains("Activity")) {
			resourceName = activityName.substring(0, activityName.indexOf("Activity"));
		} else {
			resourceName = activityName;
		}
		resourceName = Tool.tofirstLowerCase(resourceName);
		Pattern p = Pattern.compile("\\p{Upper}");
		Matcher m = p.matcher(resourceName);
		while (m.find()) {
			String s = m.group();
			resourceName = resourceName.replace(s, "_" + s.toLowerCase());
		}

		return resourceName;
	}

	public Bitmap locationImage(int drawable) {
		return new BitmapDrawable(getResources().openRawResource(drawable)).getBitmap();
	}

	public String getDay() {
		Calendar rightNow = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return fmt.format(rightNow.getTime());

	}

	/**
	 * [Simplify Toast]
	 *
	 * @param msg
	 */
	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
