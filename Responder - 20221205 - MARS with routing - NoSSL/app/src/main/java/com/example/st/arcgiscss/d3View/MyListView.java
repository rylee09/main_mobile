package com.example.st.arcgiscss.d3View;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.st.arcgiscss.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyListView extends ListView implements OnScrollListener {

	private final String TAG = "listview";

	private final int RELEASE_To_REFRESH = 0;
	private final int PULL_To_REFRESH = 1;
	private final int REFRESHING = 2;
	private final int DONE = 3;
	private final int LOADING = 4;

	//The ratio of the actual padding distance to the offset distance on the interface
	private final int RATIO = 5;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// Used to ensure that the value of startY is only recorded once in a complete touch event
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	public int firstItemIndex;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	private DateFormat ymdt;

	private boolean pauseOnScroll = true;
	private boolean pauseOnFling = true;


	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.white));
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.head, null);

		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		// arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		// headView.setPadding(0, 0, 0, 0);
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;

		ymdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
						 int arg3) {
		firstItemIndex = firstVisiableItem;
	}

	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
//			ImageLoader.getInstance().resume();
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				if (pauseOnScroll) {
//				ImageLoader.getInstance().pause();
				}
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				if (pauseOnFling) {
//				ImageLoader.getInstance().pause();
				}
				break;
		}
	}

	/**
	 * Default true
	 * @param pauseOnScroll
	 * @param pauseOnFling
	 */
	public void setOnScrollLoading(boolean pauseOnScroll,boolean pauseOnFling){
		this.pauseOnScroll = pauseOnScroll;
		this.pauseOnFling = pauseOnFling;
	}


	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (firstItemIndex == 0 && !isRecored) {
						isRecored = true;
						startY = (int) event.getY();
						Log.v(TAG, "Record the current position at the time of down");
					}
					break;

				case MotionEvent.ACTION_UP:

					if (state != REFRESHING && state != LOADING) {
						if (state == DONE) {

						}
						if (state == PULL_To_REFRESH) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "From the pull-down refresh state to the done state");
						}
						if (state == RELEASE_To_REFRESH) {
							state = REFRESHING;
							changeHeaderViewByState();
							onRefresh();

							Log.v(TAG, "By releasing the refresh state, to the done state");
						}
					}

					isRecored = false;
					isBack = false;

					break;

				case MotionEvent.ACTION_MOVE:
					int tempY = (int) event.getY();

					if (!isRecored && firstItemIndex == 0) {
						Log.v(TAG, "Record the position when move");
						isRecored = true;
						startY = tempY;
					}

					if (state != REFRESHING && isRecored && state != LOADING) {

						// Ensure that the current position is always at the head during the padding process, otherwise the list will scroll at the same time when the list is over the page.

						// You can let go and refresh it.
						if (state == RELEASE_To_REFRESH) {

							setSelection(0);

							// Pushed up, pushed to the extent that the screen is enough to cover the head, but it has not been pushed to the point of full cover.
							if (((tempY - startY) / RATIO < headContentHeight)
									&& (tempY - startY) > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();

								Log.v(TAG, "Transition from release refresh state to pull-down refresh state");
							}
							// Pushed it all at once
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();

								Log.v(TAG, "Transition from the relaxed refresh state to the done state");
							}
							// Pull down, or haven’t pushed up to the top of the screen to cover the head.
							else {
								//  No special operations, just update the value of paddingTop.
							}
						}
						// DONE or PULL_To_REFRESH state has not yet arrived when the display is released for refresh.
						if (state == PULL_To_REFRESH) {

							setSelection(0);

							// Pull down to enter the state of RELEASE_TO_REFRESH
							if ((tempY - startY) / RATIO >= headContentHeight) {
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeHeaderViewByState();

								Log.v(TAG, "Transition from done or pull-down refresh state to release refresh");
							}
							// Push up to the top
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();

								Log.v(TAG, "Transition from DOne or pull-down refresh state to done state");
							}
						}

						// Done state
						if (state == DONE) {
							if (tempY - startY > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
							}
						}

						// Update the size of the headView
						if (state == PULL_To_REFRESH) {
							headView.setPadding(0, -1 * headContentHeight
									+ (tempY - startY) / RATIO, 0, 0);

						}

						// Update paddingTop of headView
						if (state == RELEASE_To_REFRESH) {
							headView.setPadding(0, (tempY - startY) / RATIO
									- headContentHeight, 0, 0);
						}

					}

					break;
			}
		}

		return super.onTouchEvent(event);
	}

	// When the state changes, call this method to update the interface.
	private void changeHeaderViewByState() {
		switch (state) {
			case RELEASE_To_REFRESH:
				arrowImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(animation);

				tipsTextview.setText("Release refresh");

				Log.v(TAG, "Current state, release refresh");
				break;
			case PULL_To_REFRESH:
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.VISIBLE);
				// Is changed from the RELEASE_To_REFRESH state
				if (isBack) {
					isBack = false;
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(reverseAnimation);

					tipsTextview.setText("Pull down to refresh");
				} else {
					tipsTextview.setText("Pull down to refresh");
				}
				Log.v(TAG, "Current state, pull-down refresh");
				break;

			case REFRESHING:

				headView.setPadding(0, 0, 0, 0);

				progressBar.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.GONE);
				tipsTextview.setText("Refreshing...");
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				Log.v(TAG, "Current state, refreshing...");
				break;
			case DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);

				progressBar.setVisibility(View.GONE);
				arrowImageView.clearAnimation();
				arrowImageView.setImageResource(R.drawable.wb_refresh_arrow);
				tipsTextview.setText("Pull down to refresh");
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				Log.v(TAG, "Current state，done");
				break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("Recently updated:" + ymdt.format(new Date()));
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void showRefreshing(){
		state = REFRESHING;
		changeHeaderViewByState();
		onRefresh();
	}

	// This method directly copies a demo from the drop-down refresh on the network, here is the "estimate" the width and height of the headView.
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText("Recently updated:" + ymdt.format(new Date()));
		super.setAdapter(adapter);
	}

	public void setBackgroundColor(String color,String textColor){
		headView.setBackgroundColor(Color.parseColor(color));
		tipsTextview.setBackgroundColor(Color.parseColor(textColor));
		lastUpdatedTextView.setBackgroundColor(Color.parseColor(textColor));
	}
}