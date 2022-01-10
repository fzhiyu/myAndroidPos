package com.cuse.myandroidpos.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.Post.OrderAllJson.OrderAllJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.md5;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText editStationId;
    private EditText editPassWord;
    private Button btnLogin;

    private long currentTimeStamp;
    private String signature;
    private String stationId;
    private String passWord;
    public  static String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    //private LoginRequest loginRequest;
    private LoginJson loginJson;

    private HttpBinService httpBinService;
    private Retrofit retrofit;

    private ProgressDialog dialog;

    private String testFont;
    private boolean isBold, isUnderLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //长亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_login);

        //找到对应的ID
//        editStationId = findViewById(R.id.editText_sign_stationID);
//        editPassWord = findViewById(R.id.editText_sign_passWord);
//        btnLogin = findViewById(R.id.btn_sign_login);

//        retrofit = new Retrofit.Builder().baseUrl("http://paas.u-coupon.cn/pos_api/v1/")
//                .addConverterFactory(GsonConverterFactory.create()).build();
//        retrofit = new Retrofit.Builder().baseUrl("http://paas.u-coupon.cn/pos_api/v1/").build();
//        httpBinService = retrofit.create(HttpBinService.class);

//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View view) {
//
//                dialog = ProgressDialog.show(view.getContext(), "", "正在登录");
//                dialog.show();
//
//                stationId = editStationId.getText().toString();
//                passWord = editPassWord.getText().toString();
//
//                currentTimeStamp = new Date().getTime();//得到当前的时间戳，ms

                //得到imei
//                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                String imei = telephonyManager.getImei();
//
//                Call<ResponseBody> call = httpBinService.login1(stationId,passWord,
//                        currentTimeStamp / 1000 + "",imei,signature);
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        dialog.cancel();
//                        Log.i("hejun", "onResponse: " + response.code());
//                        try {
//                            String s = response.body().string();
//                            Log.i("hejun", "onResponse: " + s);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        Log.i("hejun", "onResponse: " + response.errorBody());
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });

//                loginJson = test();

//                if (loginJson != null){
//                    if (loginJson.getData().getResult() == 0) {
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.putExtra("staionId", stationId);
//                        intent.putExtra("currentTimeStamp", currentTimeStamp);
//                        dialog.cancel();
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(LoginActivity.this, loginJson.getData().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
        //登录
        Login();
    }

    //登录
    private void Login() {
        Button btn_login = findViewById(R.id.btn_sign_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postLogin();
            }
        });
    }

    //post login
    private void postLogin() {
        String token = "test456";
        String imei = "testImei2";
        stationId = "BJ001002";
        passWord = "e10adc3949ba59abbe56e057f20f883e";
        // on below line we are creating a retrofit builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://paas.u-coupon.cn/pos_api/v1/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();
        HttpBinService httpBinService = retrofit.create(HttpBinService.class);

        long timeStamp = new Date().getTime();
        //得到字符串并加密编码
        String stringBuffer = "imei" +
                imei +
                "passWord" +
                passWord +
                "stationId" +
                stationId +
                "timestamp" +
                timeStamp/1000 +
                LoginActivity.interferenceCode;
        String signature = md5.md5(stringBuffer);

        Call<LoginJson> call = httpBinService.login(stationId + "",
                passWord + "",
                timeStamp/1000 + "",
                imei + "",
                signature);

        call.enqueue(new Callback<LoginJson>() {
            @Override
            public void onResponse(Call<LoginJson> call, Response<LoginJson> response) {
                LoginJson loginJson = response.body();
                Gson gson = new Gson();
                String s = gson.toJson(loginJson);
                Log.i("responseBody", "onResponse: " + response.code());
                Log.i("stringBuffer", "" + stringBuffer);
                Log.i("应答消息", "" + s);
//                Log.i("应答编码", "" + loginJson.getCode());
//                Log.i("result", "" + loginJson.getData().getResult());
                if (loginJson == null) {
                    Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                } else if (loginJson.getData().getResult() == 0) {
                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    //传递token
                    intent.putExtra("token",loginJson.getData().getToken());
                    startActivity(intent);
                } else {
                    Tools.codeError(getApplicationContext(), loginJson.getCode());
                }
            }

            @Override
            public void onFailure(Call<LoginJson> call, Throwable t) {
            }
        });
    }

    //测试数据
    public LoginJson test() {
        //测试数据
        String s = "{\n" +
                "\t\"code\": 0,\n" +
                "\t\"message\": \"\",\n" +
                "\t\"data\": {\n" +
                "\t\t\t\"result\": 0,\n" +
                "\"message\": \"\"\n" +
                "}\n" +
                "}\n";

        return new Gson().fromJson(s, LoginJson.class);
    }
}