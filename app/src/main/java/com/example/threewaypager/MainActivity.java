package com.example.threewaypager;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.ui.main.RingWithKnob;
import com.example.threewaypager.ui.main.SectionsPagerAdapter;
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
        viewPager.addOnPageChangeListener(new UserMovesThePagesListener((RingWithKnob) findViewById(R.id.the_ring)));

    }

    private static class UserMovesThePagesListener implements ViewPager.OnPageChangeListener {

        private RingWithKnob toBeMoved;

        public UserMovesThePagesListener(RingWithKnob toBeMoved) {
            this.toBeMoved = toBeMoved;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float moveFactor = position + positionOffset;
            float from = -70;
            float to = 70;
            float newValue = from + (moveFactor * (to - from)) / 2; //TODO 2 represents pager with 3 pages (n pages -> n-1)
            Log.d("degrees", "new " + newValue);
            toBeMoved.rotateIt(newValue);

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}