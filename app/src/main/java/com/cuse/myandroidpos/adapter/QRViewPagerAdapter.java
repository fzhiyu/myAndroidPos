package com.cuse.myandroidpos.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cuse.myandroidpos.fragment.QRPagerFragment;

public class QRViewPagerAdapter extends FragmentStateAdapter {
    public QRViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //传入一个承载QR的fragment
        Fragment fragment = new QRPagerFragment();
        Bundle args = new Bundle();
        //将position传入
        args.putInt(QRPagerFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
