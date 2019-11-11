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

import com.example.threewaypager.R;

public class RingWithKnob extends View {

    Path ringPath = new Path();
    Paint ringPaint = new Paint();
    Paint knobPaint = new Paint();
    Path knobPath = new Path();
    private float newDegrees;
    Point ringCenter = new Point();
    Icon leftIcon;
    Icon centerIcon;
    Icon rightIcon;
    double iconCenterX;
    double iconCenterY;
    int maxAlpha = 255;
    int minAlpha = 0;
    private static final float iconAngle = 70; //angle at which right icon is placed, where 0 degrees is vertical line
    ArgbEvaluator colorEvaluator = new ArgbEvaluator();

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

    public void init() {
        ringCenter.set(getWidth() / 2, getHeight());
        float outer_radius;
        //Get radius from the smaller size
        if (getWidth() > getHeight()) {
            outer_radius = getWidth() / 2f;
        } else {
            outer_radius = getHeight() / 2f;
        }
        float knobStroke = 50;
        outer_radius -= knobStroke / 2; //adjust padding based on stroke of the line we are drawing ring and knob with; /2 because stroke is build on both sides of "thin line" equally
        float inner_radius = .6f * outer_radius; //determine how bold the knob will be

        //  R I N G
        {
            ringPaint.setStrokeWidth(knobStroke);
            ringPaint.setAntiAlias(true);
            ringPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            ringPaint.setColor(Color.parseColor("#EEEDEF"));
            ringPath = calculateRingSection(180, outer_radius, inner_radius, ringCenter);
        }

        //  K N O B
        {
            knobPaint.setStrokeWidth(knobStroke);
            knobPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            knobPaint.setDither(true);                    // set the dither to true
            knobPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
            knobPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
            knobPaint.setAntiAlias(true);
            knobPaint.setColor(Color.parseColor("#7A4AD1"));
            knobPath = calculateRingSection(70, outer_radius, inner_radius, ringCenter);
        }

        //  I C O N S
        {
            leftIcon = new Icon(R.drawable.ic_dollar, 160, outer_radius, inner_radius);
            centerIcon = new Icon(R.drawable.ic_euro, 90, outer_radius, inner_radius);
            rightIcon = new Icon(R.drawable.ic_yen, 20, outer_radius, inner_radius);
        }
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

    public void rotateKnob(float moveFactor) {
        float from = -iconAngle;
        float to = iconAngle;
        float newValue = from + (moveFactor * (to - from)) / 2; //TODO 2 represents pager with 3 pages (n pages -> n-1)
        Log.d("degrees", "new " + newValue);
        newDegrees = newValue;
        invalidate();
    }

    private Path calculateRingSection(float gapeAngle, float outer_radius, float inner_radius, Point center) {
        double halfAngleRadians = Math.toRadians(gapeAngle / 2);
        float a1 = (float) (inner_radius * Math.sin(halfAngleRadians));
        float a2 = (float) (outer_radius * Math.sin(halfAngleRadians));
        float b1 = (float) (inner_radius * Math.cos(halfAngleRadians));
        float b2 = (float) (outer_radius * Math.cos(halfAngleRadians));

        RectF outer_rect = new RectF(center.x - outer_radius, center.y - outer_radius, center.x + outer_radius, center.y + outer_radius);
        RectF inner_rect = new RectF(center.x - inner_radius, center.y - inner_radius, center.x + inner_radius, center.y + inner_radius);
        Path returnPath = new Path();
        returnPath.addArc(inner_rect, 270 - gapeAngle / 2, gapeAngle);
        returnPath.lineTo(center.x + a2, center.y - b2);
        returnPath.addArc(outer_rect, 270 + gapeAngle / 2, -gapeAngle);
        returnPath.lineTo(center.x - a1, center.y - b1);

        return returnPath;
    }

    private int calculateNewAlpha(float targetAngle, float currentAngle){
        int proportionalRange = 20;
        float diff = targetAngle - currentAngle;
        float lowerLimit = targetAngle - proportionalRange;
        float upperLimit = targetAngle + proportionalRange;
        Log.d("calculateNewAlpha", "diff " + diff);
        if(currentAngle > lowerLimit && currentAngle < targetAngle){
            float xxx = minAlpha + ((currentAngle - (targetAngle - proportionalRange))*(maxAlpha-minAlpha))/proportionalRange;
            Log.d("calculateNewAlpha", "proportionalRange lower" + xxx);
            return (int)xxx;
        }
        else if(currentAngle >= targetAngle && currentAngle < upperLimit ){
            float xxx = minAlpha + ((currentAngle - (targetAngle + proportionalRange))*(maxAlpha-minAlpha))/-proportionalRange;
            Log.d("calculateNewAlpha", "proportionalRange upper" + xxx);
            return (int)xxx;
        }
        else {
            Log.d("calculateNewAlpha", "min");
            return minAlpha;
        }
    }

    private class Icon {
        private Drawable iconDrawable;
        private final float iconAngle;
        private static final float angleTranslation = 90;

        Icon(@DrawableRes int id, float iconAngle, float outer_radius, float inner_radius) {
            this.iconAngle = iconAngle;
            int ringWidth = (int)(outer_radius - inner_radius);
            int halfSize = ringWidth/2;
            float iconSize = .7f*halfSize;
            float calipersRadius = inner_radius + halfSize;
            iconCenterX = ringCenter.x + calipersRadius*Math.cos(Math.toRadians(iconAngle));
            iconCenterY = ringCenter.y - calipersRadius*Math.sin(Math.toRadians(iconAngle));
            iconDrawable = getResources().getDrawable(id, null);
            iconDrawable.setTint(Color.WHITE);
            iconDrawable.setBounds((int)(iconCenterX - iconSize), (int)(iconCenterY - iconSize), (int)(iconCenterX + iconSize), (int)(iconCenterY + iconSize));
        }

        private float translateIconAngle() {
            float returnValue = -iconAngle + angleTranslation;
            Log.d("translateIconAngle", "iconAngle " + iconAngle + " -> " + returnValue);
            return returnValue;
        }

        void applyNewMove(float newDegrees) {
            int newAlpha = calculateNewAlpha(translateIconAngle(), newDegrees);
            Log.d("applyNewMove", "alpha " + newAlpha + " for icon on " + iconAngle);
//            iconDrawable.setAlpha(newAlpha);
            Integer newColor = (Integer) colorEvaluator.evaluate((float)newAlpha/maxAlpha, Color.parseColor("#BEBCD2"), Color.WHITE);
            Log.d("applyNewMove", "newAlpha/maxAlpha " + (float)newAlpha/maxAlpha + " newColor " + newColor);
            iconDrawable.setTint(newColor);
        }
    }
}
