package com.cuse.myandroidpos.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.databinding.FragmentItemDetailBinding;
import com.cuse.myandroidpos.databinding.FragmentLoginBinding;
import com.google.gson.Gson;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private EditText editStationId;
    private EditText editPassWord;
    private Button btnLogin;
    private ProgressDialog dialog;
    private HttpBinService httpBinService;
    private Retrofit retrofit;

    public  static String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";
    private String TAG = "登录";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //长亮
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //找到对应的ID
        editStationId = view.findViewById(R.id.editText_sign_stationID);
        editPassWord = view.findViewById(R.id.editText_sign_passWord);
        btnLogin = view.findViewById(R.id.btn_sign_login);

        retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        httpBinService = retrofit.create(HttpBinService.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editStationId.getText() == null || editStationId.getText().toString().equals("")){
                    Toast.makeText(view.getContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (editPassWord.getText() == null || editPassWord.getText().toString().equals("")){
                    Toast.makeText(view.getContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else
                    postLogin(view);
            }
        });
    }

    //post login
    private void postLogin(View view) {
        //弹出正在登录的弹窗
        dialog = ProgressDialog.show(view.getContext(), "", "正在登录");
        dialog.show();

        //得到需要的传送的值
        String stationId = editStationId.getText().toString();
        String passWord = editPassWord.getText().toString();
        //得到imei
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        String imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //测试
        String imei = "testImei1";

        //得到字符串并加密编码
        long timeStamp = new Date().getTime();
        String stringBuffer = "imei" +
                imei +
                "passWord" +
                passWord +
                "stationId" +
                stationId +
                "timestamp" +
                timeStamp/1000 +
                LoginActivity.interferenceCode;
        String signature = Tools.md5.md5(stringBuffer);
        //测试，用完删除
        Log.i(TAG, "stringBuffer:" + stringBuffer);
        Log.i(TAG, "signature: " + signature);

        Call<LoginJson> call = httpBinService.login(stationId + "",
                passWord + "",
                timeStamp/1000 + "",
                imei + "",
                signature);

        call.enqueue(new Callback<LoginJson>() {
            @Override
            public void onResponse(Call<LoginJson> call, Response<LoginJson> response) {
                //取消弹窗
                dialog.cancel();
                LoginJson loginJson = response.body();

                //测试，用完删除
//                Gson gson = new Gson();
//                String s = gson.toJson(loginJson);
//                String TAG = "登录";
//                Log.i(TAG, "response.code:" + response.code());
//                Log.i(TAG, "response.json" + s);

                if(response.isSuccessful() && loginJson != null){
                    if (loginJson.getCode() == 0){
                        if (loginJson.getData().getResult() == 0){
                            Bundle bundle = new Bundle();
                            bundle.putString("token", loginJson.getData().getToken());
                            Navigation.findNavController(getView())
                                    .navigate(R.id.action_login_to_list, bundle);
                        }else
                            Toast.makeText(getContext(),loginJson.getData().getMessage(),Toast.LENGTH_SHORT).show();
                    }else
                        Tools.codeError(getContext(),loginJson.getCode());
                }

            }

            @Override
            public void onFailure(Call<LoginJson> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(getContext(),"连接失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}