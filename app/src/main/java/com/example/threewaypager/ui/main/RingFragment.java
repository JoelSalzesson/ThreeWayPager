package com.example.threewaypager.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.threewaypager.R;
import com.example.threewaypager.RingKnobActivity;

public class RingFragment extends Fragment implements ViewPager.OnPageChangeListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ring, container, false);
        root.findViewById(R.id.down_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RingKnobActivity) getActivity()).doTransition();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get currently selected page from VP -> thanks to that after use hides ringknob and scrolls VP to other page and then shows ringknob again -> we know where we are and we set that value to the ringknob
        int idx = ((RingKnobActivity) getActivity()).getCurrentItemIdx();
        Log.d("onViewCreated", "idx " + idx);
        applyNewMove(idx);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //expected "moveFactor" values when 3 pages are in viewpager:
        //0 means left page is selected ... 1 center ... "maxFromViewPager" right is selected
        //so range [0...maxFromViewPager]
        float moveFactor = position + positionOffset;
        applyNewMove(moveFactor);
    }

    private void applyNewMove(float moveFactor){
        View view = getView();
        if(view != null){
            ((RingAndKnobWith3States)getView().findViewById(R.id.ring_with_knob)).onNewMove(moveFactor);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //nothing
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //nothing
    }
}