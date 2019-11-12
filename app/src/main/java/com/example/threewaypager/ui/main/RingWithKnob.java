package com.example.threewaypager.ui.main;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.R;

public final class RingWithKnob extends View implements ViewPager.OnPageChangeListener {

    Path ringPath = new Path();
    Paint ringPaint = new Paint();
    Paint knobPaint = new Paint();
    Path knobPath = new Path();
    private float newDegrees;
    Point ringCenter = new Point();
    Icon leftIcon;
    Icon centerIcon;
    Icon rightIcon;
    int maxAlpha = 255;
    int minAlpha = 0;
    private static final float iconAngle = 70; //angle at which right icon is placed, where 0 degrees is vertical line
    private static final ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private final static float knobStroke = 50; //nice round edges of the knob depends on this

    public RingWithKnob(Context context) {
        super(context);
    }

    public RingWithKnob(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RingWithKnob(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    /*
    calculates all values needed for the ring/knob to be drawn
    1. what are the radiuses (inner and outer) based on size of this RingWithKnob View
    2. then calculates Paint and Path for the ring (background of the View)
    3. then calculates Paint and Path for the knob (foreground, moving part of the View)
    4. then calculates positions and sizes for each of 3 icons on the ring
     */
    public void init() {
        // (1)
        ringCenter.set(getWidth() / 2, getHeight()); //set ring center in the middle of view (thus /2) and at the bottom of the view
        float outerRadius;
        //Get radius from the smaller size ... just in case someone defines size of this whole RingWithKnob as "portrait" rectangle
        if (getWidth() > getHeight()) {
            outerRadius = getWidth() / 2f; //take half
        } else {
            outerRadius = getHeight() / 2f; //take half
        }

        outerRadius -= knobStroke / 2; //adjust outer radius based on stroke of the line we are drawing ring and knob with; /2 because stroke is build on both sides of "thin line" equally; if we dont do this knob can go out of view area because of the stroke width
        float innerRadius = .6f * outerRadius; //determine how bold the knob will be as a fraction of outer radius

        initRing(outerRadius, innerRadius); //(2)
        initKnob(outerRadius, innerRadius); //(3)
        initIcons(outerRadius, innerRadius); //(4)
    }

    private void initRing(float outerRadius, float innerRadius){
        ringPaint.setStrokeWidth(knobStroke);
        ringPaint.setAntiAlias(true);
        ringPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        ringPaint.setColor(Color.parseColor("#EEEDEF")); //TODO could be taken from style or color resources
        ringPath = calculateRingSection(180, outerRadius, innerRadius, ringCenter); //180 means half a circle of course
    }

    private void initIcons(float outerRadius, float innerRadius){
        leftIcon = new Icon(R.drawable.ic_dollar, 160, outerRadius, innerRadius);
        centerIcon = new Icon(R.drawable.ic_euro, 90, outerRadius, innerRadius);
        rightIcon = new Icon(R.drawable.ic_yen, 20, outerRadius, innerRadius);
    }

    private void initKnob(float outerRadius, float innerRadius){
        knobPaint.setStrokeWidth(knobStroke);
        knobPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        knobPaint.setDither(true);
        knobPaint.setStrokeJoin(Paint.Join.ROUND);
        knobPaint.setStrokeCap(Paint.Cap.ROUND);
        knobPaint.setAntiAlias(true);
        knobPaint.setColor(Color.parseColor("#7A4AD1")); //TODO could be taken from style or color resources
        knobPath = calculateRingSection(70, outerRadius, innerRadius, ringCenter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(ringPath, ringPaint);
        canvas.save();
        canvas.rotate(newDegrees, ringCenter.x, ringCenter.y);
        Log.d("onDraw", "newDegrees " + newDegrees);
        canvas.drawPath(knobPath, knobPaint);
        canvas.restore();

        leftIcon.applyNewMove(newDegrees);
        leftIcon.iconDrawable.draw(canvas);

        centerIcon.applyNewMove(newDegrees);
        centerIcon.iconDrawable.draw(canvas);

        rightIcon.applyNewMove(newDegrees);
        rightIcon.iconDrawable.draw(canvas);
    }

    /**
     * calculates part of ring based on angle
     * @param gapeAngle how wide the ring part will be in degrees; e.g. 180 will give half of circle
     * @return calculated ring path to be drawn
     */
    private Path calculateRingSection(float gapeAngle, float outer_radius, float inner_radius, Point center) {
        double halfAngleRadians = Math.toRadians(gapeAngle / 2);
        //calculate lines to connect outer arc with inner arc to close the path
        float posX1 = (float) (inner_radius * Math.sin(halfAngleRadians));
        float posX2 = (float) (outer_radius * Math.sin(halfAngleRadians));
        float posY1 = (float) (inner_radius * Math.cos(halfAngleRadians));
        float posY2 = (float) (outer_radius * Math.cos(halfAngleRadians));

        RectF outer_rect = new RectF(center.x - outer_radius, center.y - outer_radius, center.x + outer_radius, center.y + outer_radius);
        RectF inner_rect = new RectF(center.x - inner_radius, center.y - inner_radius, center.x + inner_radius, center.y + inner_radius);
        Path returnPath = new Path();
        returnPath.addArc(inner_rect, 270 - gapeAngle / 2, gapeAngle); //270 comes from how "addArc" interprets given angle
        returnPath.lineTo(center.x + posX2, center.y - posY2);
        returnPath.addArc(outer_rect, 270 + gapeAngle / 2, -gapeAngle); //270 comes from how "addArc" interprets given angle
        returnPath.lineTo(center.x - posX1, center.y - posY1);

        return returnPath;
    }

    private int calculateNewAlpha(float targetAngle, float currentAngle) {
        int proportionalRange = 20;
        float diff = targetAngle - currentAngle;
        float lowerLimit = targetAngle - proportionalRange;
        float upperLimit = targetAngle + proportionalRange;
        Log.d("calculateNewAlpha", "diff " + diff);
        if (currentAngle > lowerLimit && currentAngle < targetAngle) {
            float xxx = minAlpha + ((currentAngle - (targetAngle - proportionalRange)) * (maxAlpha - minAlpha)) / proportionalRange;
            Log.d("calculateNewAlpha", "proportionalRange lower" + xxx);
            return (int) xxx;
        } else if (currentAngle >= targetAngle && currentAngle < upperLimit) {
            float xxx = minAlpha + ((currentAngle - (targetAngle + proportionalRange)) * (maxAlpha - minAlpha)) / -proportionalRange;
            Log.d("calculateNewAlpha", "proportionalRange upper" + xxx);
            return (int) xxx;
        } else {
            Log.d("calculateNewAlpha", "min");
            return minAlpha;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        float moveFactor = position + positionOffset;
        float from = -iconAngle;
        float to = iconAngle;
        float newValue = from + (moveFactor * (to - from)) / 2; //TODO 2 represents pager with 3 pages (n pages -> n-1)
        Log.d("degrees", "new " + newValue);
        newDegrees = newValue;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        //nothing
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //nothing
    }

    private class Icon {
        private Drawable iconDrawable;
        private final float iconAngle; //in degrees; where on the ring this icon will be placed; kind of polar coordinate of the icon
        private static final float angleTranslation = 90; //in degrees; needed to translate how angle is passed from outside (it comes as value used to rotate canvas)

        Icon(@DrawableRes int id, float iconAngle, float outer_radius, float inner_radius) {
            this.iconAngle = iconAngle;
            int ringWidth = (int) (outer_radius - inner_radius);
            int halfSize = ringWidth / 2;
            float iconSize = .7f * halfSize;
            float calipersRadius = inner_radius + halfSize;
            double iconCenterX = ringCenter.x + calipersRadius * Math.cos(Math.toRadians(iconAngle));
            double iconCenterY = ringCenter.y - calipersRadius * Math.sin(Math.toRadians(iconAngle));
            iconDrawable = getResources().getDrawable(id, null);
            iconDrawable.setTint(Color.WHITE);
            iconDrawable.setBounds((int) (iconCenterX - iconSize), (int) (iconCenterY - iconSize), (int) (iconCenterX + iconSize), (int) (iconCenterY + iconSize));
        }

        private float translateIconAngle() {
            float returnValue = -iconAngle + angleTranslation;
            Log.d("translateIconAngle", "iconAngle " + iconAngle + " -> " + returnValue);
            return returnValue;
        }

        void applyNewMove(float newDegrees) {
            int newAlpha = calculateNewAlpha(translateIconAngle(), newDegrees);
            Log.d("applyNewMove", "alpha " + newAlpha + " for icon on " + iconAngle);
            Integer newColor = (Integer) colorEvaluator.evaluate((float) newAlpha / maxAlpha, Color.parseColor("#BEBCD2"), Color.WHITE);
            Log.d("applyNewMove", "newAlpha/maxAlpha " + (float) newAlpha / maxAlpha + " newColor " + newColor);
            iconDrawable.setTint(newColor);
        }
    }
}
