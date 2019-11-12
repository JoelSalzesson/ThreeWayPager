package com.example.threewaypager.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.threewaypager.R;
import com.example.threewaypager.RingKnobActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuFragment extends Fragment {

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        root.findViewById(R.id.up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RingKnobActivity)getActivity()).doTransition();
            }
        });
        return root;
    }
}