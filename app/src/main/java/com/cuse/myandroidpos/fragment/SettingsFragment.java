package com.cuse.myandroidpos.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.cuse.myandroidpos.MD5AndBase64;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.Push.PushJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.google.gson.Gson;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String token = "test123";
    private long currentTimeStamp;
    private String signature;
    private final String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private Retrofit retrofit;
    private HttpBinService httpBinService;

    private PushJson result;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference sync = findPreference("sync");
        sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //得到字符串并加密
                currentTimeStamp = new Date().getTime();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("timestamp");
                stringBuffer.append(currentTimeStamp / 1000);
                stringBuffer.append("token");
                stringBuffer.append(token);
                stringBuffer.append(interferenceCode);
                signature = MD5AndBase64.md5(stringBuffer.toString());
                Log.i("hejun", "onPreferenceClick: " + currentTimeStamp / 1000);
                Log.i("hejun", "onPreferenceClick: " + stringBuffer.toString());
                Log.i("hejun", "onPreferenceClick: " + signature);

                retrofit = new Retrofit.Builder().baseUrl("http://paas.u-coupon.cn/pos_api/v1/")
                        .addConverterFactory(GsonConverterFactory.create()).build();
                httpBinService = retrofit.create(HttpBinService.class);
                Call<PushJson> call = httpBinService.push(token, currentTimeStamp / 1000 + "", signature);
                call.enqueue(new Callback<PushJson>() {
                    @Override
                    public void onResponse(Call<PushJson> call, Response<PushJson> response) {
                        PushJson pushJson = response.body();
                        Log.i("hejun", "onResponse: " + pushJson.getCode());
                        if (pushJson.getCode() == 0){
                            if (pushJson.getData().getResult() == 0)
                                Toast.makeText(getContext(),"推送成功",Toast.LENGTH_SHORT);
                            else
                                Toast.makeText(getContext(),"推送失败",Toast.LENGTH_SHORT);
                        }else
                            Tools.codeError(getContext(), pushJson.getCode());
                    }

                    @Override
                    public void onFailure(Call<PushJson> call, Throwable t) {
                        Toast.makeText(getContext(),"连接失败",Toast.LENGTH_SHORT);
                    }
                });
                return false;
            }
        });

        //得到switch的值
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Boolean checkPrint = prefs.getBoolean("print",false);
                Toast.makeText(getActivity(), "" + checkPrint, Toast.LENGTH_SHORT).show();
            }
        });
        
    }
}