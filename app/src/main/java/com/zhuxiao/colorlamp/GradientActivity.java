package com.zhuxiao.colorlamp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import com.zhuxiao.colorlamp.R;

@SuppressLint("Recycle")
public class GradientActivity extends FragmentActivity {
	private MyLinearLayout view;
	private BubbleLayout2 my_Bubble_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main2);
		view = (MyLinearLayout) findViewById(R.id.my_view);
		view.setAct(this);
		my_Bubble_view = (BubbleLayout2) findViewById(R.id.my_Bubble_view);
		my_Bubble_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					my_Bubble_view.setViewRax((int) event.getRawX());
					my_Bubble_view.setViewRay((int) event.getRawY());
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = my_Bubble_view.getViewRax() - (int) event.getRawX();
					int dy = my_Bubble_view.getViewRay() - (int) event.getRawY();
					if(Math.abs(dx)>2 || Math.abs(dy)>2){
						my_Bubble_view.setViewRax(-1);
						my_Bubble_view.setViewRay(-1);
					}
					break;
				case MotionEvent.ACTION_UP:
					if(my_Bubble_view.getViewRax()!=-1 && my_Bubble_view.getViewRay()!=-1){
						my_Bubble_view.judgeIsInCircle();
					}
					break;
				}
				view.onTouchEvent(event);
				return true;
			}
		});
	}
}
