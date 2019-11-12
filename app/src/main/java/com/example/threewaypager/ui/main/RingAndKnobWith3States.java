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

import java.util.ArrayList;
import java.util.List;

public final class RingAndKnobWith3States extends View {

    Path ringPath = new Path();
    Paint ringPaint = new Paint();
    Paint knobPaint = new Paint();
    Path knobPath = new Path();
    private float newDegrees;
    private final Point ringCenter = new Point();
    private int numberOfStates = 3; //only 3 states of the knob are expected to work: left, center, right; other numbers not tested
    private final int maxFromViewPager = numberOfStates - 1; //to interpret what viewpager gives us as position it's moved we need to know the max value it will send us; that value equals number of pages VP has minus 1; so 3 pages -> we get 2
    private List<Icon> icons = new ArrayList<>(numberOfStates);
    private static final float theAngle = 70; //angle at which right icon is placed, where 0 degrees is vertical line
    private static final ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private final static float knobStroke = 50; //nice round edges of the knob depends on this

    public RingAndKnobWith3States(Context context) {
        super(context);
    }

    public RingAndKnobWith3States(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RingAndKnobWith3States(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void startOffScreen(){
//        setTranslationY(getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
//        startOffScreen();
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
        Icon leftIcon = new Icon(R.drawable.ic_dollar, 160, outerRadius, innerRadius);
        Icon centerIcon = new Icon(R.drawable.ic_euro, 90, outerRadius, innerRadius);
        Icon rightIcon = new Icon(R.drawable.ic_yen, 20, outerRadius, innerRadius);
        icons.add(leftIcon);
        icons.add(centerIcon);
        icons.add(rightIcon);
    }

    private void initKnob(float outerRadius, float innerRadius){
        knobPaint.setStrokeWidth(knobStroke);
        knobPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        knobPaint.setStrokeJoin(Paint.Join.ROUND);
        knobPaint.setStrokeCap(Paint.Cap.ROUND);
        knobPaint.setAntiAlias(true);
        knobPaint.setColor(Color.parseColor("#7A4AD1")); //TODO could be taken from style or color resources
        knobPath = calculateRingSection(70, outerRadius, innerRadius, ringCenter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw ring background
        canvas.drawPath(ringPath, ringPaint);
        canvas.save();

        //rotate and draw foreground knob
        canvas.rotate(newDegrees, ringCenter.x, ringCenter.y);
        Log.d("onDraw", "newDegrees " + newDegrees);
        canvas.drawPath(knobPath, knobPaint);
        canvas.restore();

        //and draw all icons
        for (Icon icon : icons){
            icon.applyNewMove(newDegrees);
            icon.iconDrawable.draw(canvas);
        }
    }

    /**
     * @param gapeAngle how wide the ring part will be in degrees; e.g. 180 will give half of circle
     * @return calculates closed path for part of ring based on angle, its outer and inner radius and center point
     */
    private Path calculateRingSection(float gapeAngle, float outer_radius, float inner_radius, Point center) {
        double halfAngleRadians = Math.toRadians(gapeAngle / 2);
        //calculate lines to connect outer arc with inner arc to close the path
        float posX1 = (float) (inner_radius * Math.sin(halfAngleRadians));
        float posX2 = (float) (outer_radius * Math.sin(halfAngleRadians));
        float posY1 = (float) (inner_radius * Math.cos(halfAngleRadians));
        float posY2 = (float) (outer_radius * Math.cos(halfAngleRadians));

        RectF outerRect = new RectF(center.x - outer_radius, center.y - outer_radius, center.x + outer_radius, center.y + outer_radius);
        RectF innerRect = new RectF(center.x - inner_radius, center.y - inner_radius, center.x + inner_radius, center.y + inner_radius);
        Path returnPath = new Path();
        returnPath.addArc(innerRect, 270 - gapeAngle / 2, gapeAngle); //270 comes from how "addArc" interprets given angle
        returnPath.lineTo(center.x + posX2, center.y - posY2);
        returnPath.addArc(outerRect, 270 + gapeAngle / 2, -gapeAngle); //270 comes from how "addArc" interprets given angle
        returnPath.lineTo(center.x - posX1, center.y - posY1);

        return returnPath;
    }

    /*
    translate viewpager scroll state into angle
    knob will be rotated based on that angle
    icons color will react on that angle
     */
    public void onNewMove(float moveFactor) {
        float from = -theAngle;
        float to = theAngle;

        //values from moveFactor should be translated to angle range [from...to]
        float newValue = from + (moveFactor * (to - from)) / maxFromViewPager;
        Log.d("degrees", "new " + newValue);
        newDegrees = newValue;
        invalidate();
    }

    private class Icon {
        private Drawable iconDrawable;
        private final float iconAngle; //in degrees; where on the ring this icon will be placed; kind of polar coordinate of the icon
        private static final float angleTranslation = 90; //in degrees; needed to translate how angle is passed from outside (it comes as value used to rotate canvas)
        //magic values for nice calculation of new icon color based on angle:
        private static final int justSomeMax = 100;
        private static final int justTheMin = 0;
        private static final int proportionalRange = 20; //in degrees where color will fade down; outside of this range [iconAngle - proportionalRange ... iconAngle + proportionalRange] the color will just stay the same

        Icon(@DrawableRes int id, float iconAngle, float outer_radius, float inner_radius) {
            this.iconAngle = iconAngle; //polar coordinates -> angle
            int ringWidth = (int) (outer_radius - inner_radius); //we need this to roughly estimate where icon center should be placed and how big it should be
            int halfSize = ringWidth / 2; //take half -> used
            float iconSize = .7f * halfSize; //scale it down to 70% not to be drawn outside of arc
            float calipersRadius = inner_radius + halfSize; //polar coordinates -> radius
            //calculate icon center respective to ring center using polar coordinates
            double iconCenterX = ringCenter.x + calipersRadius * Math.cos(Math.toRadians(iconAngle));
            double iconCenterY = ringCenter.y - calipersRadius * Math.sin(Math.toRadians(iconAngle));
            iconDrawable = getResources().getDrawable(id, null);
            iconDrawable.setTint(Color.WHITE); //TODO could be taken from style or color resources
            iconDrawable.setBounds((int) (iconCenterX - iconSize), (int) (iconCenterY - iconSize), (int) (iconCenterX + iconSize), (int) (iconCenterY + iconSize));
        }

        private float translateIconAngle() {
            float returnValue = -iconAngle + angleTranslation;
            Log.d("translateIconAngle", "theAngle " + iconAngle + " -> " + returnValue);
            return returnValue;
        }

        /*
        returns fraction [0...1] to be later used in colorEvaluator to move from one color to another
         */
        private float calculateFractionForIconColorChange(float targetAngle, float currentAngle) {
            float newValue;
            float lowerLimit = targetAngle - proportionalRange;
            float upperLimit = targetAngle + proportionalRange;

            if (currentAngle > lowerLimit && currentAngle < upperLimit) {
                float howFarFromTarget = Math.abs(targetAngle - currentAngle);
                Log.d("calculateFraction"+iconAngle, "howFarFromTarget " + howFarFromTarget);
                newValue = justTheMin + ((targetAngle - howFarFromTarget - lowerLimit) * (justSomeMax - justTheMin)) / (targetAngle - lowerLimit);
            } else {
                Log.d("calculateFraction"+iconAngle, "justTheMin");
                newValue = justTheMin;
            }
            Log.d("calculateFraction"+iconAngle, "newValue " + newValue);
            return newValue / justSomeMax;
        }

        void applyNewMove(float newDegrees) {
            float fraction = calculateFractionForIconColorChange(translateIconAngle(), newDegrees);
            Log.d("applyNewMove", "fraction " + fraction + " for icon on " + iconAngle);
            Integer newColor = (Integer) colorEvaluator.evaluate(fraction, Color.parseColor("#BEBCD2"), Color.WHITE); //TODO could be taken from style or color resources
            iconDrawable.setTint(newColor);
        }
    }
}
