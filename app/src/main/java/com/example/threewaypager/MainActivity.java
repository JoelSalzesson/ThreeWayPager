package com.example.threewaypager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.ui.main.GummyDot;
import com.example.threewaypager.ui.main.RingAndKnobWith3States;
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
        viewPager.addOnPageChangeListener((RingAndKnobWith3States) findViewById(R.id.ring_with_knob));
        viewPager.addOnPageChangeListener((GummyDot) findViewById(R.id.gummy_dot));
    }
}