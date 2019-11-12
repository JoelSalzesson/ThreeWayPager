package com.example.threewaypager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.ui.main.GummyDot;
import com.example.threewaypager.ui.main.MenuFragment;
import com.example.threewaypager.ui.main.RingFragment;
import com.example.threewaypager.ui.main.SectionsPagerAdapter;

public class RingKnobActivity extends AppCompatActivity {

    private MenuFragment menuFragment;
    private RingFragment ringFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        menuFragment = MenuFragment.newInstance();
        ringFragment = RingFragment.newInstance();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottom_thing_container, menuFragment , "menuFragment");
        fragmentTransaction.commit();

        viewPager.addOnPageChangeListener((GummyDot) findViewById(R.id.gummy_dot));
        viewPager.addOnPageChangeListener(ringFragment);
    }

    public void doTransition() {
        if(getSupportFragmentManager().findFragmentByTag("menuFragment") != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);
            fragmentTransaction.replace(R.id.bottom_thing_container, ringFragment , "ringFragment");
            fragmentTransaction.commit();
        }
        else if(getSupportFragmentManager().findFragmentByTag("ringFragment") != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);
            fragmentTransaction.replace(R.id.bottom_thing_container, menuFragment , "menuFragment");
            fragmentTransaction.commit();
        }
    }

    public int getCurrentItemIdx(){
        ViewPager viewPager = findViewById(R.id.view_pager);
        return viewPager.getCurrentItem();
    }
}