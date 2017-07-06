package com.zhuxiao.colorlamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 模拟海底气泡上升的自定义控件
 * 同时点击产生、消失气泡
 * 
 * @author zhuxiao
 */
public class BubbleLayout2 extends View {
	private boolean isLog=true;
	private List<Bubble> bubbles = new ArrayList<Bubble>();
	private Random random = new Random();//生成随机数
	private int width, height;
	private boolean starting_push = true;//开始压入泡泡
	private int startRawX=0;//获取含有状态栏、图标栏、应用内容的y轴
	private int startRawY=0;
	private BlurMaskFilter maskFilter;
	private Paint paint;
	private int status_bar_height=50;//状态栏的高度
	private int bubble_num=15;//泡泡的数量
	List<Bubble> delList = new ArrayList<Bubble>();
	public BubbleLayout2(Context context) {
		super(context);
		setClickable(true);
	}

	public BubbleLayout2(Context context, AttributeSet attrs) {
		super(context, attrs);		
		setClickable(true);
	}

	public BubbleLayout2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		setClickable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(paint==null){//第一次初始化
			setLayerType(LAYER_TYPE_SOFTWARE, null);
			width = getWidth();
			height = getHeight();
			paint = new Paint();
			paint.setStyle(Style.STROKE);
			if(maskFilter==null)
			maskFilter = new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER);
			paint.setMaskFilter(maskFilter);
			paint.setStrokeWidth(8);
			paint.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰 
			paint.setColor(Color.WHITE);//灰白色
			paint.setAlpha(80);//设置不透明度：透明为0，完全不透明为255
		}
		//如果需要压入泡泡
		if (!starting_push) {
			Bubble bubble = new Bubble();
			int radius = random.nextInt(120);
			while (radius <= 60) {
				radius = random.nextInt(120);
			}
			float speedY = random.nextFloat()*2;
			while (speedY < 1) {
				speedY = random.nextFloat()*2;
			}
			while (speedY == 0) {
				speedY = random.nextFloat()-0.5f;
			}
			bubble.setRadius(1);
			bubble.setPreRadius(radius);//设置预定半径
			bubble.setSpeedY(speedY);
			bubble.setX(startRawX);
			bubble.setY(startRawY-status_bar_height);
			float speedX = random.nextFloat()-0.5f;
			while (speedX == 0) {
				speedX = random.nextFloat()-0.5f;
			}
			bubble.setSpeedX(speedX*2);
			bubbles.add(bubble);
			starting_push = true;
		}
		List<Bubble> list = new ArrayList<Bubble>(bubbles);
		//依次设置气泡的坐标，以及边界值判断
		for (Bubble bubble : list) 
		{
			//泡泡产生、半径随着速度增大到预定的半径
			if(bubble.getPreRadius()>0f && bubble.getRadius()<bubble.getPreRadius()){
				float currRadius=  (bubble.getRadius()+bubble.getSpeedY()*2);
				if(currRadius>bubble.getPreRadius()){currRadius = bubble.getPreRadius();}
				bubble.setRadius(currRadius);
			}else if(bubble.getPreRadius()<=0f){
				float currRadius=  (bubble.getRadius()-bubble.getSpeedY()*3);
				if(currRadius<0){//消失
					delList.add(bubble);//加入删除的集合
				}
				else{
					bubble.setRadius(currRadius);
				}
			}
			//碰到上边界从数组中移除
			if (bubble.getY() - bubble.getSpeedY() <= -bubble.getRadius()*2) 
			{
				removeBubble(bubble);
			}
			//碰到左边界从数组中移除
			else if(bubble.getX() - bubble.getRadius() <= -bubble.getRadius()*2)
			{
				removeBubble(bubble);
			}
			//碰到右边界从数组中移除
			else if(bubble.getX() >= width+bubble.getRadius())
			{
				removeBubble(bubble);
			}
			else //未到边界
			{
				int i = bubbles.indexOf(bubble);
//				if (bubble.getX() + bubble.getSpeedX() <= bubble.getRadius()) {//x轴的左边界
//					bubble.setX(bubble.getRadius());
//				} else if (bubble.getX() + bubble.getSpeedX() >= width - bubble.getRadius()) {//x轴的右边界
//					bubble.setX(width - bubble.getRadius());
//				} else {
//					bubble.setX(bubble.getX() + bubble.getSpeedX());
//				}
				bubble.setX(bubble.getX() + bubble.getSpeedX());
				bubble.setY(bubble.getY() - bubble.getSpeedY());
				bubbles.set(i, bubble);
				canvas.drawCircle(bubble.getX(), bubble.getY(),bubble.getRadius(), paint);
			}
		}
		//移除要删除的泡泡
		bubbles.removeAll(delList);
		delList.clear();
		//刷新屏幕
		invalidate();
	}
	
	/**
	 * 移去bubble，并且放至list末尾
	 * @param bubble
	 */
	private void removeBubble(Bubble bubble) {
		bubbles.remove(bubble);
		if(bubbles.size()<=15){
			int nextIntX = random.nextInt(width);
			if(nextIntX<bubble.getRadius()){
				nextIntX = (int) bubble.getRadius()+1;
			}else if(nextIntX > width){
				nextIntX = width-(int) bubble.getRadius();
			}
			bubble.setX(nextIntX);
			bubble.setY(height+bubble.getRadius()*2);
			bubbles.add(bubbles.size(), bubble);//如果添加到集合末尾 则不会 移动元素
		}
	}
		public void setViewRax(int x){
			startRawX = x;
		}
		public int getViewRax(){
			return startRawX;
		}
		public void setViewRay(int y){
			startRawY = y;
		}
		public int getViewRay(){
			return startRawY;
		}
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			setViewRax((int) event.getRawX());
//			setViewRay((int) event.getRawY());
//			break;
//		case MotionEvent.ACTION_MOVE:
//			int dx = getViewRax() - (int) event.getRawX();
//			int dy = getViewRay() - (int) event.getRawY();
//			if(Math.abs(dx)>2 || Math.abs(dy)>2){
//				setViewRax(-1);
//				setViewRay(-1);
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			if(getViewRax()!=-1 && getViewRay()!=-1){
//				judgeIsInCircle();
//			}
//			break;
//		}
//		return true;
//	}
	
	/**
	 * 判断是否在圆圈内,如果在则消失，否则就在创建
	 */
	public void judgeIsInCircle() {
		boolean isIn=false;
		List<Bubble> list = new ArrayList<Bubble>(bubbles);
		for(Bubble bubble:list){
			float x2 = bubble.getX();
			float radius = bubble.getRadius();
			float y2 = bubble.getY();
			//如果在此圆范围内，则消失
			if(x2-radius<startRawX && startRawX<x2+radius  &&  y2-radius<(startRawY-status_bar_height) && (startRawY-status_bar_height)<y2+radius)
			{
				bubble.setPreRadius(0.0f);
				isIn=true;
				break;
			}
		}
		//如果不在 气泡范围内，并且泡泡数不超过bubble_num ，则产生新的
		if(!isIn && bubbles.size()<=bubble_num){
			if(isLog)
			Log.e("isLog", startRawY+":x="+startRawX);
			popBubble();
		}
		invalidate();
	}

	/**
	 * 手指点击抬起 产生气泡
	 */
	private void popBubble() {
		starting_push = false;
	}
	private class Bubble {
		//气泡半径 
		private float radius;
		//气泡预计半径 
		private float preRadius;
		//上升速度
		private float speedY;
		//平移速度
		private float speedX;
		//气泡x坐标
		private float x;
		// 气泡y坐标
		private float y;
		public float getRadius() {
			return radius;
		}
		public void setRadius(float radius) {
			this.radius = radius;
		}
		public float getX() {
			return x;
		}
		public void setX(float x) {
			this.x = x;
		}
		public float getY() {
			return y;
		}
		public void setY(float y) {
			this.y = y;
		}

		public float getSpeedY() {
			return speedY;
		}

		public void setSpeedY(float speedY) {
			this.speedY = speedY;
		}

		public float getSpeedX() {
			return speedX;
		}

		public void setSpeedX(float speedX) {
			this.speedX = speedX;
		}
		public float getPreRadius() {
			return preRadius;
		}
		public void setPreRadius(float preRadius) {
			this.preRadius = preRadius;
		}
	}
}