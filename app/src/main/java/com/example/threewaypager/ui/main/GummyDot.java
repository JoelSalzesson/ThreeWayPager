package com.example.threewaypager.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.R;

public class GummyDot extends View implements ViewPager.OnPageChangeListener {

    private static final String TAG = GummyDot.class.getSimpleName();
    private int position;
    private float positionOffset;
    private Paint backgroundDotPaint = new Paint();
    private Paint movingDotPaint = new Paint();
    private float baseDotRadius;
    private float bigDotWidth;
    private static final float mid = .5f;

    public GummyDot(Context context) {
        super(context);
    }

    public GummyDot(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GummyDot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    public void init() {
        baseDotRadius = getHeight() / 3f;
        bigDotWidth = 2 * baseDotRadius;

        {
            backgroundDotPaint.setStyle(Paint.Style.FILL);
            backgroundDotPaint.setColor(getResources().getColor(R.color.grey));
        }

        {
            movingDotPaint.setAntiAlias(true);
            movingDotPaint.setStyle(Paint.Style.FILL);
            movingDotPaint.setColor(getResources().getColor(R.color.purple));
        }
    }

    private void addDot(Canvas canvas, Point center, float sizeX, float sizeY, Paint paint) {
        float _1_3_height = getHeight() / 3f;
        canvas.drawRoundRect(center.x - sizeX, center.y - sizeY, center.x + sizeX, center.y + sizeY, _1_3_height, _1_3_height, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point dot1 = new Point(getWidth() / 6, getHeight() / 2);
        Point dot2 = new Point(3 * getWidth() / 6, getHeight() / 2);
        Point dot3 = new Point(5 * getWidth() / 6, getHeight() / 2);
        addDot(canvas, dot1, baseDotRadius, getHeight() / 3f, backgroundDotPaint);
        addDot(canvas, dot2, baseDotRadius, getHeight() / 3f, backgroundDotPaint);
        addDot(canvas, dot3, baseDotRadius, getHeight() / 3f, backgroundDotPaint);

        //  M O V I N G    D O T
        {
            Point movingDot = new Point(getWidth() / 6, getHeight() / 2);
            movingDot.x = (int) calculateNewPosX(position + positionOffset, dot1.x, dot3.x);
            float movingDotRadius = calculateNewSizeX(position, positionOffset);
            addDot(canvas, movingDot, movingDotRadius, getHeight() / 3f, movingDotPaint);
        }
    }

    private float calculateNewPosX(float factor, int from, int to) {
        return from + (factor * (to - from)) / 2;
    }

    private float calculateNewSizeX(int position, float positionOffset) {
        float totalPos = position + positionOffset;
        float howFarFromMid = Math.abs(position + mid - totalPos);
        Log.d(TAG, "howFarFromMid " + howFarFromMid);
        float newSize = bigDotWidth + (howFarFromMid * (baseDotRadius - bigDotWidth)) / mid;
        Log.d(TAG, "newSize " + newSize);
        return newSize;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "position " + position + " positionOffset " + positionOffset + " positionOffsetPixels " + positionOffsetPixels);
        this.position = position;
        this.positionOffset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
