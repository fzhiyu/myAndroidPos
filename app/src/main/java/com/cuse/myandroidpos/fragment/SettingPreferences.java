package com.cuse.myandroidpos.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.Push.PushJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.activity.MainActivity;
import com.google.gson.Gson;


import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingPreferences extends PreferenceFragmentCompat {

    private static final String TAG = "setting";
    private String token;
    private long timeStamp;
    private String signature;
    private final String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private Retrofit retrofit;
    private HttpBinService httpBinService;

    private Dialog dialog;



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

//        Preference sync = findPreference("sync");
//        token = ((MainActivity)getActivity()).getToken();

//        sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                dialog = ProgressDialog.show(getContext(),"","正在测试");
//                //测试，用完删除
//                token = "test123";
//
//                //得到字符串并加密
//                timeStamp = new Date().getTime();
//                String stringBuffer = "timestamp" +
//                        timeStamp / 1000 +
//                        "token" +
//                        token +
//                        interferenceCode;
//                signature = Tools.md5.md5(stringBuffer);
//                //测试，用完删除
//                Log.i(TAG, "stringBuffer: " + stringBuffer);
//                Log.i(TAG, "signature): " + signature);
//
//                retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
//                        .addConverterFactory(GsonConverterFactory.create()).build();
//                httpBinService = retrofit.create(HttpBinService.class);
//                Call<PushJson> call = httpBinService.push(token, timeStamp / 1000 + "", signature);
//                call.enqueue(new Callback<PushJson>() {
//                    @Override
//                    public void onResponse(Call<PushJson> call, Response<PushJson> response) {
//                        dialog.cancel();
//                        PushJson pushJson = response.body();
//                        //测试，用完删除
//                        Log.i(TAG, "response.code: " + response.code());
//                        Gson gson = new Gson();
//                        Log.i(TAG, "pushJson " + gson.toJson(pushJson));
//
//                        if (response.isSuccessful() && pushJson != null && pushJson.getCode() == 0) {
//                            Toast.makeText(getContext(), "推送成功", Toast.LENGTH_SHORT).show();
//                        } else
//                            Toast.makeText(getContext(), "推送失败", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<PushJson> call, Throwable t) {
//                        dialog.cancel();
//                        Toast.makeText(getContext(),"连接失败",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return false;
//            }
//        });

        
    }
}