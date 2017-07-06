package com.zhuxiao.colorlamp;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.zhuxiao.colorlamp.utils.BrightnessTools;

@SuppressWarnings("deprecation")
public class MyLinearLayout extends View {
	private Activity mact;
	private LinearGradient gradinet;
	private Paint paint;
	private int width;
	private int height;
	private int start_evaluate = 0xFF4876FF; 
	private int end_evaluate = 0xFF4682B4;
	private int colorArrNum;
	private List<ColorArr> colorList = new ArrayList<MyLinearLayout.ColorArr>();
	ArgbEvaluator evaluator;
	public MyLinearLayout(Context context) {
		super(context);
		init(context);
	}

	public void setAct(Activity act){
		evaluator = new ArgbEvaluator();
		mact =act;
	}
	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		//00F5FF 00E5EE
		//CDAF95 CD9B1D
		//FF8C69 FF8247
		//EEB4B4 EEA9B8
		colorList.add(new ColorArr(0xFF4876FF,0xFF4682B4));
		colorList.add(new ColorArr(0xFFCDAF95,0xFFCD9B1D));
		colorList.add(new ColorArr(0xFFFF8C69,0xFFFF8247));
		colorList.add(new ColorArr(0xFFEEB4B4,0xFFEEA9B8));
		colorList.add(new ColorArr(0xFF8B475D,0xFF8B3E2F));
		colorList.add(new ColorArr(0xFF556B2F,0xFF548B54));
		colorArrNum = colorList.size()-1;
	}
	
	public void setGradient(int start,int end){
		this.start_evaluate = start;
		this.end_evaluate = end;
		gradinet = new LinearGradient(0, 0, width, height, start, end, Shader.TileMode.CLAMP);
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (paint == null) {
			paint = new Paint();
		}
		if (gradinet == null) {
			gradinet = new LinearGradient(0, 0, width, height, start_evaluate, end_evaluate, Shader.TileMode.MIRROR);
		}
		paint.setShader(gradinet);
		canvas.drawRect(0, 0, width	, height, paint);
	}
	
	private static final int BASE_VALUE = 200;
	private int startX=0;
	private int startY=0;
	private int colorNum=0;
	private int oldYvalue=-1;
	private boolean isXSlide=true;
	private boolean isYSlide=true;
	private ValueAnimator changeColorAnimator;
	private int dx;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = (int) event.getX();
				startY = (int) event.getY();
			case MotionEvent.ACTION_MOVE:
				 int endX = (int) event.getX();
				 int endY = (int) event.getY();
				 dx = endX-startX;
				int dy = endY-startY;
//				//x轴滑动切换背景颜色
				if(Math.abs(dx)>Math.abs(dy) && isXSlide && Math.abs(dx)>BASE_VALUE){
					//边界-100 100 
					if(dx>0){//右滑 colorNum++
						colorNum = colorNum==colorArrNum?colorNum=0:colorNum+1;
					}else if(dx<0){
						colorNum = colorNum==0?colorNum=colorArrNum:colorNum-1;
					}
					Log.e("colorNum", "colorNum:"+colorNum+":"+dx);
					if(isYSlide){
						isYSlide = false;
					}
					if(isXSlide){
						isXSlide = false;
					}
						///开始执行
						if(changeColorAnimator==null){//复用一个anim
							changeColorAnimator = ValueAnimator.ofFloat(0f,1f);
							changeColorAnimator.setDuration(800l);
							changeColorAnimator.addUpdateListener(new AnimatorUpdateListener() {
								@Override
								public void onAnimationUpdate(ValueAnimator animation) {
									float  changeV = (Float) animation.getAnimatedValue();
									if(evaluator==null)
										evaluator = new ArgbEvaluator();
									//获取当前colorNum所对应的 一双过渡 终值
									ColorArr currentColorArr = colorList.get(colorNum);
									//计算出过渡值
									int evaluate1 = (Integer) evaluator.evaluate(changeV, start_evaluate, currentColorArr.lightColorValue);//参数1变化百分比，参数2
									int evaluate2 = (Integer) evaluator.evaluate(changeV, end_evaluate, currentColorArr.heavyColorValue);
									//给View设置gradient，同时等于1记录最终的值
									MyLinearLayout.this.setGradient(evaluate1,evaluate2);//上半部分、下半部分
									if(((int)changeV)==1){
										start_evaluate = evaluate1;
										end_evaluate = evaluate2;
									}
								}	
							});
							changeColorAnimator.setRepeatCount(0);
						}
							changeColorAnimator.start();
							startX = endX;
							startY = endY;
				}else if(Math.abs(dx)<Math.abs(dy) && isYSlide && Math.abs(dy)>8){//y轴滑动调节屏幕亮度
					//如果是增加亮度 则
					int currentRoom = oldYvalue==-1?BrightnessTools.getScreenBrightness(mact):oldYvalue;
					if(dy>0){//调低
						currentRoom = (int) (currentRoom-(dy/1000f)*255);
					}else{//增强
						currentRoom = (int) (currentRoom-(dy/1000f)*255);
					}
					//如果当前亮度最小了 则为5
					currentRoom = currentRoom<5?5:currentRoom;
					//如果当前亮度最大了 则为255
					currentRoom = currentRoom>250?250:currentRoom;
					BrightnessTools.setBrightness(mact, currentRoom);
					oldYvalue =currentRoom;
					startX = endX;
					startY = endY;
					isXSlide = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				startX=0;
				startY=0;
				isXSlide = true;
				isYSlide = true;
				break;
		}
		return true;
	}
	private class ColorArr{
		public int lightColorValue;
		public int heavyColorValue;
		public ColorArr(int lightColorValue, int heavyColorValue) {
			super();
			this.lightColorValue = lightColorValue;
			this.heavyColorValue = heavyColorValue;
		}
	}
}
