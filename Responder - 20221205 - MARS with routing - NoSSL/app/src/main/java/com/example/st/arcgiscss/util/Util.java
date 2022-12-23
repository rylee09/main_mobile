package com.example.st.arcgiscss.util;

import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	private static Util util;
	public static int flag = 0;
	private double r = 6371.004;

	private Util() {

	}

	public static Util getInstance() {
		if (util == null) {
			util = new Util();
		}
		return util;
	}

	/**
	 *
	 * @return
	 */
	public boolean hasSDCard() {
		boolean b = false;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			b = true;
		}
		return b;
	}

	/**
	 *
	 * @return
	 */
	public String getExtPath() {
		String path = "";
		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	/**
	 *
	 * @param mActivity
	 * @return
	 */
	public String getPackagePath(Context mActivity) {
		return mActivity.getFilesDir().toString();
	}

	/**
	 *
	 * @param url
	 * @return
	 */
	public String getImageName(String url) {
		String imageName = "";
		if (url != null) {
			imageName = url.substring(url.lastIndexOf("/") + 1);
		}
		return imageName;
	}

	/**
	 *
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public String getDistance(double lat1, double lon1, double lat2, double lon2) {
		String string;

		if (lat2 == 0 && lon2 == 0) {
			string = "No data";
		} else {
			double distance = 0;
			distance = 2
					* r
					* Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2),
					2)
					+ Math.cos(lat1)
					* Math.cos(lat2)
					* Math.pow(Math.sin((lon1 - lon2) / 2), 2)));
			DecimalFormat df = new DecimalFormat("##.##");
			string = df.format(distance)+"km";
		}
		return string;
	}

	/**

	 * @param mobiles
	 * @return
	 */
	public boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * @return
	 */
	public boolean isEmail(String email) {
		Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 */
	public boolean equalStr(String numOrStr) {
		boolean flag = true;
		char str = numOrStr.charAt(0);
		for (int i = 0; i < numOrStr.length(); i++) {
			if (str != numOrStr.charAt(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	public boolean isOrderNumeric(String numOrStr) {
		boolean flag = true;
		boolean isNumeric = true;
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	public boolean isOrderNumeric_(String numOrStr) {
		boolean flag = true;
		boolean isNumeric = true;
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;

	}


	public static void disableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(false);
					spinner.setEnabled(false);

				} else if (v instanceof ListView) {
					((ListView) v).setClickable(false);
					((ListView) v).setEnabled(false);

				} else {
					disableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(false);
				((EditText) v).setClickable(false);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(false);
			}
		}
	}

	public static void ableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(true);
					spinner.setEnabled(true);

				} else if (v instanceof ListView) {
					((ListView) v).setClickable(true);
					((ListView) v).setEnabled(true);

				} else {
					ableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(true);
				((EditText) v).setClickable(true);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(true);
			}
		}
	}


	public static void hideIM(Context context,View edt) {
		try {
			InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			IBinder windowToken = edt.getWindowToken();
			if (windowToken != null) {
				im.hideSoftInputFromWindow(windowToken, 0);
//             System.out.println("done");
			}
		} catch (Exception e) {
		}
	}



}