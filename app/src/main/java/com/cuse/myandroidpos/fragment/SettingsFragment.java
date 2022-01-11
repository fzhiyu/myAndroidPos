package com.cuse.myandroidpos.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.cuse.myandroidpos.MD5AndBase64;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.Push.PushJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;
import com.cuse.myandroidpos.databinding.FragmentSettingBinding;
import com.cuse.myandroidpos.md5;
import com.cuse.myandroidpos.PosWebSocket.wsInfo;
import com.cuse.myandroidpos.PosWebSocket.CreateWebSockets;
import com.google.gson.Gson;
import com.alibaba.fastjson.JSON;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.gusavila92.websocketclient.WebSocketClient;

public class SettingsFragment extends PreferenceFragmentCompat {

//    private FragmentSettingBinding binding;
    private String token;
    private long timeStamp;
    private String signature;
    private final String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private Retrofit retrofit;
    private HttpBinService httpBinService;

    private PushJson result;

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        binding = FragmentSettingBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference sync = findPreference("sync");
        sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                token = ((MainActivity)getActivity()).getToken();
                //得到字符串并加密
                timeStamp = new Date().getTime();
                String stringBuffer = "timestamp" +
                        timeStamp / 1000 +
                        "token" +
                        token +
                        LoginActivity.interferenceCode;
                signature = md5.md5(stringBuffer);
//                Log.i("hejun", "onPreferenceClick: " + currentTimeStamp / 1000);
//                Log.i("hejun", "onPreferenceClick: " + stringBuffer.toString());
//                Log.i("hejun", "onPreferenceClick: " + signature);

                retrofit = new Retrofit.Builder().baseUrl("http://paas.u-coupon.cn/pos_api/v1/")
                        .addConverterFactory(GsonConverterFactory.create()).build();
                httpBinService = retrofit.create(HttpBinService.class);
                Call<PushJson> call = httpBinService.push(token, timeStamp / 1000 + "", signature);
                call.enqueue(new Callback<PushJson>() {
                    @Override
                    public void onResponse(Call<PushJson> call, Response<PushJson> response) {
                        PushJson pushJson = response.body();
                        Log.i("hejun", "onResponse: " + pushJson.getCode());
                        if (pushJson == null) {
                            Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
                        } else if(pushJson.getCode() == 0) {
                            Toast.makeText(getContext(),pushJson.getData().getResult(),Toast.LENGTH_SHORT).show();
                        } else {
                            Tools.codeError(getContext(), pushJson.getCode());
                        }
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