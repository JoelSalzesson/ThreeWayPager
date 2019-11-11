package com.example.threewaypager.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RingWithKnob extends View {

    Path ringPath = new Path();
    Paint ringPaint = new Paint();
    Paint knobPaint = new Paint();
    Path knobPath = new Path();
    private float newDegrees;
    Point ringCenter = new Point();

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
            ringPaint.setColor(Color.RED);
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
            knobPath = calculateRingSection(70, outer_radius, inner_radius, ringCenter);
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
    }

    public void rotateIt(float degrees) {
        newDegrees = degrees;
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
}
