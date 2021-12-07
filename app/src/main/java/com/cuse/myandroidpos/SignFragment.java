package com.cuse.myandroidpos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.Post.LoginJson.LoginRequest;
import com.cuse.myandroidpos.databinding.FragmentSignBinding;
import com.google.gson.Gson;

import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignFragment extends Fragment {

    private FragmentSignBinding binding;

    private EditText editStaionId;
    private EditText editPassWord;
    private Button btnLogin;

    private long currentTimeStamp;
    private String signature;
    private String staionId;
    private String passWord;
    private String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";

    private LoginRequest loginRequest;
    private LoginJson loginJson;
    private HttpBinService httpBinService;
    private Retrofit retrofit;

    public SignFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignBinding.inflate(inflater,container,false);
        View rootView = binding.getRoot();

        //找到对应的ID
        editStaionId = rootView.findViewById(R.id.editText_sign_stationID);
        editPassWord = rootView.findViewById(R.id.editText_sign_passWord);
        btnLogin = rootView.findViewById(R.id.btn_sign_login);

        //得到输入的值
        staionId = editStaionId.getText().toString();
        passWord = editPassWord.getText().toString();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeStamp = new Date().getTime();//得到当前的时间戳，ms

                //得到字符串并加密编码
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("passWord");
                stringBuffer.append(passWord);
                stringBuffer.append("staionId");
                stringBuffer.append(staionId);
                stringBuffer.append("timestamp");
                stringBuffer.append(currentTimeStamp / 1000);
                stringBuffer.append(interferenceCode);
                signature = MD5AndBase64.md5(stringBuffer.toString());

                //得到提交的json数据 route
                loginRequest = new LoginRequest();
                loginRequest.setStationId(staionId);
                loginRequest.setPassWord(passWord);
                loginRequest.setTimestamp(currentTimeStamp / 1000 + "");
                loginRequest.setSignature(signature);

                Gson gson = new Gson();
                String route = gson.toJson(loginRequest);

//                //post
//                retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
//                        .addConverterFactory(GsonConverterFactory.create()).build();
//                httpBinService = retrofit.create(HttpBinService.class);
//
//                RequestBody body = RequestBody.create(MediaType.parse("application/json"),route);
//                Call<LoginJson> call = httpBinService.login(body);
//                call.enqueue(new Callback<LoginJson>() {
//                    @Override
//                    public void onResponse(Call<LoginJson> call, Response<LoginJson> response) {
//                        loginJson = response.body();
//                    }
//
//                    @Override
//                    public void onFailure(Call<LoginJson> call, Throwable t) {
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

                loginJson = new Gson().fromJson(s,LoginJson.class);

                if (loginJson.getData().getResult() == 0){
                    Navigation.findNavController(getView()).navigate(R.id.sign_to_list);
                }else {
                    Toast.makeText(getActivity(),loginJson.getData().getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });

        return rootView;
    }
}