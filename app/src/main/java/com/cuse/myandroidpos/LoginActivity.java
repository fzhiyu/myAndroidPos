package com.cuse.myandroidpos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.cuse.myandroidpos.Post.LoginJson.LoginRequest;
import com.cuse.myandroidpos.activity.TextActivity;
import com.cuse.myandroidpos.utils.SunmiPrintHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity{

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
        editStationId = findViewById(R.id.editText_sign_stationID);
        editPassWord = findViewById(R.id.editText_sign_passWord);
        btnLogin = findViewById(R.id.btn_sign_login);

//        retrofit = new Retrofit.Builder().baseUrl("http://paas.u-coupon.cn/pos_api/v1/")
//                .addConverterFactory(GsonConverterFactory.create()).build();
        retrofit = new Retrofit.Builder().baseUrl("http://paas.u-coupon.cn/pos_api/v1/").build();
        httpBinService = retrofit.create(HttpBinService.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                dialog = ProgressDialog.show(view.getContext(), "", "正在登录");
                dialog.show();

                stationId = editStationId.getText().toString();
                passWord = editPassWord.getText().toString();

                stationId = "BJ001001";
                passWord = "e10adc3949ba59abbe56e057f20f883e";

                currentTimeStamp = new Date().getTime();//得到当前的时间戳，ms
                //得到字符串并加密编码
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("passWord");
                stringBuffer.append(passWord);
                stringBuffer.append("staionId");
                stringBuffer.append(stationId);
                stringBuffer.append("timestamp");
                stringBuffer.append(currentTimeStamp / 1000);
                stringBuffer.append(interferenceCode);
                signature = MD5AndBase64.md5(stringBuffer.toString());

                //得到imei
//                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
////                String imei = telephonyManager.getImei();

                Call<ResponseBody> call = httpBinService.login1(stationId,passWord,
                        currentTimeStamp / 1000 + "","123",signature);
                Log.i("hejun", "onClick: " + call.toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.cancel();
                        Log.i("hejun", "onResponse: " + response.code());
                        try {
                            String s = response.body().string();
                            Log.i("hejun", "onResponse: " + s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.i("hejun", "onResponse: " + response.errorBody());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

//                loginPost(route);

                if (loginJson != null){
                    if (loginJson.getData().getResult() == 0) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("staionId", stationId);
                        intent.putExtra("currentTimeStamp", currentTimeStamp);
                        dialog.cancel();
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, loginJson.getData().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        Button btnLogin_tmp = findViewById(R.id.btn_sign_login_tmp);

        btnLogin_tmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        testFont = null;
        isBold = true;
        isUnderLine = true;

        Button btn_print_test = findViewById(R.id.btn_print_test);
        btn_print_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, TextActivity.class);
                startActivity(intent);
            }
        });
    }

}