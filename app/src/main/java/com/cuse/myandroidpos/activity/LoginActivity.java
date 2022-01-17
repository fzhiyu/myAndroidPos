package com.cuse.myandroidpos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login";

    private EditText editStationId;
    private EditText editPassWord;
    private Button btnLogin;
    private ProgressDialog dialog;
    private HttpBinService httpBinService;
    private Retrofit retrofit;
    private TextToSpeech textToSpeech;

    public  static String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private boolean isBold, isUnderLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //长亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_login);

        this.setTitle("登录");


        //找到对应的ID
        editStationId = findViewById(R.id.editText_sign_stationID);
        editPassWord = findViewById(R.id.editText_sign_passWord);
        btnLogin = findViewById(R.id.btn_sign_login);

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

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });
        Button btn_speech = findViewById(R.id.btn_speech);
        btn_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "您有新订单";
                textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
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
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //测试
        imei = "testImei2";

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
                Gson gson = new Gson();
                String s = gson.toJson(loginJson);
                Log.i(TAG, "response.code:" + response.code());
                Log.i(TAG, "response.json" + s);

                if(response.isSuccessful() && loginJson != null){
                    if (loginJson.getCode() == 0){
                        if (loginJson.getData().getResult() == 0){
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            //传递token
                            intent.putExtra("token",loginJson.getData().getToken());
                            intent.putExtra("stationId",stationId);
                            startActivity(intent);
                        }else
                            Toast.makeText(LoginActivity.this,loginJson.getData().getMessage(),Toast.LENGTH_SHORT).show();
                    }else
                        Tools.codeError(LoginActivity.this,loginJson.getCode());
                }

            }

            @Override
            public void onFailure(Call<LoginJson> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(LoginActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


}