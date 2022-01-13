package com.cuse.myandroidpos.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderRefundJson.OrderRefundJson;
import com.cuse.myandroidpos.Post.getSmsCode.SmsCodeJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.databinding.FragmentBackProcessBinding;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackProcessFragment extends Fragment{
    //倒计时秒数
    private int countSeconds = 300;
    private EditText mobile_phone, smsCode;
    private Button getSmsCode, btn_apply;
    private String token;
    private String TAG = "退单";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        FragmentBackProcessBinding binding = FragmentBackProcessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        //获取验证码
        getSmsCode();

        //申请退单
        applyBack();
    }

    //申请退单
    private void applyBack () {
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReFund();

            }
        });
    }

    //post 退单 id
    private void postReFund () {
//        String oilOrderId = "2182823938284";
        String oilOrderId = getArguments().getString("oilOrderId");
        Log.e("oilOrderId", "postReFund: " + oilOrderId);
        String smsCode = "505703";
        String refundReason = "退单";
        token = ((MainActivity)getActivity()).getToken();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpBinService httpBinService = retrofit.create(HttpBinService.class);
        long timeStamp = new Date().getTime();
        String stringBuffer = "oilOrderId" +
                        oilOrderId +
                        "refundReason" +
                        refundReason +
                        "smsCode" +
                        smsCode +
                        "timestamp" +
                        timeStamp / 1000 +
                        "token" +
                        token +
                        LoginActivity.interferenceCode;
        String signature = Tools.md5.md5(stringBuffer);

        Call<OrderRefundJson> call = httpBinService.refundNew(token
                , smsCode
                , oilOrderId
                , timeStamp/1000 + ""
                , refundReason
                , signature);

        call.enqueue(new Callback<OrderRefundJson>() {
            @Override
            public void onResponse(Call<OrderRefundJson> call, Response<OrderRefundJson> response) {
                OrderRefundJson orderRefundJson = response.body();
                Log.e(TAG, "onResponse: " + response.raw());
                Log.e(TAG, "onResponse: " + JSON.toJSONString(orderRefundJson));
                Log.e(TAG, "onResponse: " + response.raw());
                if (orderRefundJson == null) {
                    Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
                } else if(orderRefundJson.getCode() == 0) {
                    Log.e(TAG, "onResponse: " + orderRefundJson.getData().getMessage());
                    Toast.makeText(getContext(),orderRefundJson.getData()
                      .getMessage(),Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(getView()).navigate(R.id.process_to_back, null);
                } else {
                    Tools.codeError(getContext(), orderRefundJson.getCode());
                }
            }

            @Override
            public void onFailure(Call<OrderRefundJson> call, Throwable t) {

            }
        });
    }

    private void getSmsCode() {
        getSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否有手机号
                String mobilePhone = mobile_phone.getText().toString();
                getMobilePhone(mobilePhone);
                startCountBack();
            }
        });
    }

    //判断是否有手机号
    private void getMobilePhone (String mobilePhone) {
        if ("".equals(mobilePhone)) {
            Log.i(TAG, "MobilePhone: " + mobilePhone);
            new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                    .setTitle("提示")
                    .setMessage("手机号不能为空")
                    .setCancelable(true)
                    .show();
        } else if (!isMobileNO(mobilePhone)) {
            new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                    .setTitle("提示")
                    .setMessage("请输入正确的手机号")
                    .setCancelable(true)
                    .show();
        } else {
            Log.e(TAG, "getMobilePhone: 输入了正确的手机号");
            postSmsCode();
        }
    }

    //获取验证码信息
    private void postSmsCode () {
        token = ((MainActivity)getActivity()).getToken();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpBinService httpBinService = retrofit.create(HttpBinService.class);
        long timeStamp = new Date().getTime();
        String stringBuffer =
                "timestamp" +
                timeStamp / 1000 +
                "token" +
                token +
                LoginActivity.interferenceCode;
        String signature = Tools.md5.md5(stringBuffer);

        Call<SmsCodeJson> call = httpBinService.getSmsCode(token
                , timeStamp/1000 + ""
                , signature);

        call.enqueue(new Callback<SmsCodeJson>() {
           @Override
           public void onResponse(Call<SmsCodeJson> call, Response<SmsCodeJson> response) {
               SmsCodeJson smsCodeJson = response.body();
//               Log.e(TAG, "onResponse: " + JSON.toJSONString(smsCodeJson));
//               Log.e(TAG, "onResponse: " + response.raw());
               if (smsCodeJson == null) {
                   Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
               } else if(smsCodeJson.getCode() == 0) {
                   Toast.makeText(getContext(),smsCodeJson.getData().getMessage(),Toast.LENGTH_SHORT).show();

               } else {
                   Tools.codeError(getContext(), smsCodeJson.getCode());
               }
           }

           @Override
           public void onFailure(Call<SmsCodeJson> call, Throwable t) {

           }
       });
    }

    private void initView(View view) {
        mobile_phone = (EditText) view.findViewById(R.id.mobile_phone);
        getSmsCode = (Button) view.findViewById(R.id.getSmsCode);
        smsCode = (EditText) view.findViewById(R.id.smsCode);
        btn_apply = (Button) view.findViewById(R.id.btn_apply);
    }

    private final Handler mCountHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (countSeconds > 0) {
                --countSeconds;
                getSmsCode.setText("(" + countSeconds + ")s后重新获取验证码");
                mCountHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                getSmsCode.setText("请重新获取验证码");
            }
            return true;
        }
    });

    //使用正则表达式判断电话号码
    public static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    private void startCountBack() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSmsCode.setText(countSeconds + "");
                mCountHandler.sendEmptyMessage(0);
            }
        });
    }
}