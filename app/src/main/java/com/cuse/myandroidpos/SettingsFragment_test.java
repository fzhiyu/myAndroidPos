package com.cuse.myandroidpos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.Push.Push;
import com.cuse.myandroidpos.Post.Push.PushRequest;
import com.google.gson.Gson;

import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsFragment_test extends PreferenceFragmentCompat {

    private String stationId = "BJ001001";
    private long currentTimeStamp;
    private String signature;
    private final String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private Retrofit retrofit;
    private HttpBinService httpBinService;

    private PushRequest pushRequest;
    private Push result;

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
                stringBuffer.append("stationId");
                stringBuffer.append(stationId);
                stringBuffer.append("timestamp");
                stringBuffer.append(currentTimeStamp / 1000);
                stringBuffer.append(interferenceCode);
                signature = MD5AndBase64.md5(stringBuffer.toString());

                //得到提交的json数据 route
                pushRequest = new PushRequest();
                pushRequest.setStationId(stationId);
                pushRequest.setTimestamp(currentTimeStamp / 1000 + "");
                pushRequest.setSignature(signature);

                String route = new Gson().toJson(pushRequest);


                retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                        .addConverterFactory(GsonConverterFactory.create()).build();
                httpBinService = retrofit.create(HttpBinService.class);

//                RequestBody body = RequestBody.create(MediaType.parse("application/json"),route);
//                Call<Push> call = httpBinService.push(body);
//                call.enqueue(new Callback<Push>() {
//                    @Override
//                    public void onResponse(Call<Push> call, Response<Push> response) {
//                        result = response.body();
//                    }
//
//                    @Override
//                    public void onFailure(Call<Push> call, Throwable t) {
//
//                    }
//                });

                //测试数据
                String s = "{\n" +
                        "\t\"code\": 0,\n" +
                        "\t\"message\": \"\",\n" +
                        "\t\"data\": {\n" +
                        "\t\t\t\"result\": 0,\n" +
                        "\"message\": \"\"\n" +
                        "}\n" +
                        "}\n";
                Gson gson = new Gson();
                result = gson.fromJson(s,Push.class);

                if (result.getCode() == 0 && result.getData().getResult() ==0)
                    Toast.makeText(getActivity(),"同步成功",Toast.LENGTH_LONG).show();
                return false;
            }
        });

//得到switch的值
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        Boolean checkPrint = prefs.getBoolean("print",false);
//        Toast.makeText(getActivity(), "" + checkPrint, Toast.LENGTH_SHORT).show();
    }
}