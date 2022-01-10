package com.cuse.myandroidpos.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.databinding.FragmentBackProcessBinding;
import com.cuse.myandroidpos.databinding.FragmentItemDetailBinding;

public class BackProcessFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentBackProcessBinding binding = FragmentBackProcessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}