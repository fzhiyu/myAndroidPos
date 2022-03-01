package com.cuse.myandroidpos.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.adapter.QRViewPagerAdapter;

import me.relex.circleindicator.CircleIndicator3;

public class QRFragment extends Fragment {

    private ViewPager2 mViewPaper;
    private QRViewPagerAdapter qrViewPagerAdapter;
    private CircleIndicator3 indicator;

    public QRFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        qrViewPagerAdapter = new QRViewPagerAdapter(this);
        mViewPaper = view.findViewById(R.id.viewpager_qr);
        mViewPaper.setAdapter(qrViewPagerAdapter);

        indicator = view.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPaper);

    }
}