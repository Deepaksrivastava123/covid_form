package com.sdbiosensor.covicatch.screens.instructionpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class PagerFragment extends Fragment {

    private View view;

    public PagerFragment() { }

    public static PagerFragment newInstance() {
        return new PagerFragment();
    }

    public static Fragment newInstance(int viewId) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt("viewId", viewId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(inflater, container);
        return view;
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        Bundle args = getArguments();
        if (args != null) {
            int viewId = args.getInt("viewId");
            view = inflater.inflate(viewId, container, false);
        }
    }

}
