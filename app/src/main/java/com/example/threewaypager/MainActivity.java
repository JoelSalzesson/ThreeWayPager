package com.example.threewaypager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        final FloatingActionButton fab = findViewById(R.id.fab);

        final View toBeMoved = findViewById(R.id.the_slice);
        toBeMoved.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toBeMoved.setPivotX(0);
                toBeMoved.setPivotY(toBeMoved.getHeight());
            }
        });

        viewPager.addOnPageChangeListener(new UserMovesThePagesListener(findViewById(R.id.the_slice)));
    }

    private static class UserMovesThePagesListener implements ViewPager.OnPageChangeListener {

        private View toBeMoved;

        public UserMovesThePagesListener(View toBeMoved) {
            this.toBeMoved = toBeMoved;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float moveFactor = position + positionOffset;
            float newValue = -90f + (moveFactor * 90) / 2; //TODO 2 represents pager with 3 pages (n pages -> n-1)
            Log.d("degrees", "new " + newValue);
            toBeMoved.setRotation(newValue);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}