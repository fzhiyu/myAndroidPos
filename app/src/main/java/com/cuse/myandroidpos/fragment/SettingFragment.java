package com.cuse.myandroidpos.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuse.myandroidpos.R;

public class SettingFragment extends Fragment {

    private int versionCode = 0;//版本号，一般用于内部
    private String versionName = "";//版本名，一般展示给用户看，eg: V1.0

    private TextView tvVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvVersionName = getView().findViewById(R.id.tv_setting_versionName);

        //得到版本名和版本号
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getContext().getPackageName(),0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvVersionName.setText("V" + versionName);

    }
}