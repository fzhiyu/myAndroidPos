package com.cuse.myandroidpos.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cuse.myandroidpos.QRCodeUtil;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.activity.MainActivity;


public class QRPagerFragment extends Fragment {

    public static final String ARG_OBJECT = "object";
    private ImageView imageView;
    private String stationId;


    public QRPagerFragment() {
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
        return inflater.inflate(R.layout.fragment_qr_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageView = view.findViewById(R.id.img_qr);

        //得到position
        Bundle args = getArguments();
        int position = args.getInt(ARG_OBJECT);

        if (position == 2){ //滑动到了stationID那一页
            //得到login传来的stationID
            if (getActivity() != null) {
                stationId = ((MainActivity) getActivity()).getStationId();
            }
            //获取图片Bitmap
            Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(stationId, 360,360);
            imageView.setImageBitmap(mBitmap);
        }
//        else {
//            imageView.setImageResource(R.drawable.ic_miniprog_qr_360);
//
//        }

    }


}