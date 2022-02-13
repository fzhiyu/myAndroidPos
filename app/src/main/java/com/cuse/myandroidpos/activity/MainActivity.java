package com.cuse.myandroidpos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.cuse.myandroidpos.CustomPopDialog2;
import com.cuse.myandroidpos.MyListData;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.QRCodeUtil;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.databinding.ActivityItemDetailBinding;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ArrayList<MyListData> myListData;
    private View decorView;
    private TextToSpeech textToSpeech;
    private Button btn;

    private String token;
    private String stationId;
    private String TAG = "mainActivity";
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActivityItemDetailBinding binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding.getRoot() -> app:id/container -> activity_item_detail

        //得到loginActivity传过来的token
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        stationId = intent.getStringExtra("stationId");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_item);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.
                Builder(navController.getGraph())
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }

    //屏蔽多任务键
    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_item);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    //隐藏导航栏
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Tools.hideBottomUIMenu(getWindow().getDecorView());
        }
    }

    //stationId
    public String getStationId() {
        return stationId;
    }
    //向fragment传递token
    public String getToken(){
        return token;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back){
//            logout();
            new AlertDialog.Builder(this)
//                    .setTitle("确认退出")
                    .setMessage("确认退出登录吗？")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();
            return true;
        } else if (item.getItemId() == R.id.QRCode){
            //尝试得到android id
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            }catch (Exception ex){
                imei = "0000000000000000";
            }
            //imei为空
            if (imei == null ){
                imei = "0000000000000000";
            }
            //获取图片Bitmap
            Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(imei, 360,360);
            //创建自定义的dialog  CustomPopDialog2
            CustomPopDialog2.Builder dialogBuild = new CustomPopDialog2.Builder(MainActivity.this);
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

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_HOME) {
            Toast.makeText(getApplicationContext(), "测试", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}