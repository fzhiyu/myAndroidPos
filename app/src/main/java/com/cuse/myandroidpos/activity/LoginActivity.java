package com.cuse.myandroidpos.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cuse.myandroidpos.CustomPopDialog2;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.QRCodeUtil;
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
    private TextView tvAndroidId;
    private Button btnLogin;
    private ImageButton imgBtPswShow;
    private ProgressDialog dialog;
    private HttpBinService httpBinService;
    private Retrofit retrofit;

    public  static String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private boolean isPswVisible = false;
    private String passWord;
    private String imei;

    private boolean internet = true;//网络连接参量
    private TextToSpeech textToSpeech;

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
        tvAndroidId = findViewById(R.id.tv_login_androidID);
        imgBtPswShow = findViewById(R.id.imgBt_login_visible);

        //设置隐藏密码
        PasswordTransformationMethod method2 = PasswordTransformationMethod.getInstance();
        editPassWord.setTransformationMethod(method2);

        //得到android id
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i(TAG, "imei: " + imei);

        //设置底部的textView，显示android id
        if (imei != null || !imei.equals(""))
            tvAndroidId.setText("id: " + imei);
        else
            tvAndroidId.setText("id: " + 0000000000000000);

        retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        httpBinService = retrofit.create(HttpBinService.class);

        //得到网络是否连接
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // network available
                Log.e(TAG, "onAvailable: ");
                internet = true;
            }

            @Override
            public void onLost(Network network) {
                // network unavailable
                Log.e(TAG, "onLost: ");
                internet = false;
            }
        };
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.registerDefaultNetworkCallback(networkCallback);

        //登录按钮
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //网络无连接
                if (internet){
                    //密码用户名不能为空
                    if (editStationId.getText() == null || editStationId.getText().toString().equals("")){
                        Toast.makeText(view.getContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }else if (editPassWord.getText() == null || editPassWord.getText().toString().equals("")){
                        Toast.makeText(view.getContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }else
                        postLogin(view);
                }else{
                    Toast.makeText(LoginActivity.this,"网络无连接", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

        //退出按钮
        Button btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });

        //密码是否可见按钮
        imgBtPswShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPawVisible();
            }
        });

//        initSpeech();
//        Button btnTestTTs = findViewById(R.id.btn_login_testTTs);
//        btnTestTTs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.i(TAG, "onClick: ");
//                String s = "新订单,    用户" + Tools.numberToChineseNumber("4572") + "汽油95号300.00元";
//                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        });
    }

    //post login
    private void postLogin(View view) {
        //弹出正在登录的弹窗
        dialog = ProgressDialog.show(view.getContext(), "", "正在登录");
        dialog.show();

        //得到需要的传送的值
        String stationId = editStationId.getText().toString();
        String passWord = editPassWord.getText().toString();

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

    //密码输入框是否可见
    private void setPawVisible(){
        isPswVisible = !isPswVisible;
        if (isPswVisible){
            //设置可见图片
            imgBtPswShow.setImageResource(R.drawable.ic_password_visible);
            //显示密码
            HideReturnsTransformationMethod method1 = HideReturnsTransformationMethod.getInstance();
            editPassWord.setTransformationMethod(method1);
        }else {
            //设置不可见图片
            imgBtPswShow.setImageResource(R.drawable.ic_password_invisible);
            //隐藏密码
            PasswordTransformationMethod method2 = PasswordTransformationMethod.getInstance();
            editPassWord.setTransformationMethod(method2);
        }

        editPassWord.setSelection(editPassWord.getText().toString().length());
    }

    //初始化语音
    private void initSpeech() {
        textToSpeech = new TextToSpeech(LoginActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.setPitch(1f);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.QRCode){
            if (imei == null ){
                imei = "0000000000000000";
            }
            //获取图片Bitmap
            Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(imei, 360,360);
            //创建自定义的dialog  CustomPopDialog2
            CustomPopDialog2.Builder dialogBuild = new CustomPopDialog2.Builder(LoginActivity.this);
            //设置图像
            dialogBuild.setImage(mBitmap);
            CustomPopDialog2 dialog = dialogBuild.create();
            dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
            dialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
}