package com.sdbiosensor.covicatch.screens.instructionpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sdbiosensor.covicatch.R;


public class PagerFragment extends Fragment {

    private View view;

    public PagerFragment() { }

    public static PagerFragment newInstance() {
        return new PagerFragment();
    }

    public static Fragment newInstance(int stringId, int imageId) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt("stringId", stringId);
        args.putInt("imageId", imageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_instruction, container, false);
        initView();
        return view;
    }

    private void initView() {
        Bundle args = getArguments();
        if (args != null) {
            int stringId = args.getInt("stringId");
            int imageId = args.getInt("imageId");

            ((ImageView) view.findViewById(R.id.image)).setImageResource(imageId);
            ((TextView) view.findViewById(R.id.text)).setText(stringId);
        }
    }

}
