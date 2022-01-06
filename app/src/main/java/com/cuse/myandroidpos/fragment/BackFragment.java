package com.cuse.myandroidpos.fragment;

import static android.content.ContentValues.TAG;

import static com.cuse.myandroidpos.TimeKey.getTodayTimestamp;
import static com.cuse.myandroidpos.TimeKey.getWeekTimestamp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cuse.myandroidpos.BackAdapter;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderRefundJson.OrderRefundJson;
import com.cuse.myandroidpos.Post.RefundAllJson.OilOrder;
import com.cuse.myandroidpos.Post.RefundAllJson.RefundAllJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.databinding.FragmentBackBinding;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import top.androidman.SuperButton;


public class BackFragment extends Fragment implements View.OnTouchListener{

    private static String TAG = "Refund";

    private FragmentBackBinding binding;
    private EditText searStartTime;
    private EditText searEndTime;
    private SuperButton btnPastHour;
    private SuperButton btnToday;
    private SuperButton btnWeek;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private BackAdapter backAdapter;

    private Dialog dialog;

    private long currentTimeStamp;//当前时间戳
    private long startTimeStamp;
    private long endTimeStamp;
    private int start = 1;
    private String signature;
    private int count = 20;
    private String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";
    private String token = "test123";

    private RefundAllJson refundAllJson;
    private List<OilOrder> list ;
    private HttpBinService httpBinService;
    private Retrofit retrofit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_back, container, false);
        binding = FragmentBackBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recyclerview
        recyclerView = binding.backList;
        //文字框
        searStartTime = view.findViewById(R.id.sear_startTime);
        searEndTime = view.findViewById(R.id.sear_endTime);
        searStartTime.setOnTouchListener(this);
        searEndTime.setOnTouchListener(this);

        //功能按钮
        btnPastHour = view.findViewById(R.id.back_past_hour);
        btnToday = view.findViewById(R.id.back_today);
        btnWeek = view.findViewById(R.id.back_week);
        btnSearch = view.findViewById(R.id.back_search);

        //列表RecyclerView
        setBackRecyclerView();

        //按钮的点击事件
        setButton(view);

    }


    public void setBackRecyclerView () {
        //recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);//列表竖向

        initData();
        list = refundAllJson.getData().getOilOrder();
        backAdapter = new BackAdapter(list,getActivity());
        recyclerView.setAdapter(backAdapter);//填入数据

        backAdapter.setBackRecyclerItemClickListener(new BackAdapter.OnBackRecyclerItemClickListener() {
            @Override
            public void OnBackRecyclerItemClick(int position) {
                Bundle  bundle = new Bundle();
                //使用Serializable来传递对象，传递的对象需要继承Serializable
                bundle.putSerializable("RefundOilOrder",refundAllJson.getData().getOilOrder().get(position));
                //bundle.putString("RefundOilOrder", refundAllJson.getData().getOilOrder().get(position).toString());
                Navigation.findNavController(getView()).navigate(R.id.back_to_backDetail,bundle);
            }
        });//点击Item
    }

    public void refundAllPost () {
                //得到字符串并加密编码
                currentTimeStamp = new Date().getTime();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("count");
                stringBuffer.append(count);
                stringBuffer.append("endTime");
                stringBuffer.append(endTimeStamp / 1000);
                stringBuffer.append("start");
                stringBuffer.append(start);
                stringBuffer.append("startTime");
                stringBuffer.append(startTimeStamp / 1000);
                stringBuffer.append(interferenceCode);
                stringBuffer.append("timestamp");
                stringBuffer.append(currentTimeStamp / 1000);
                stringBuffer.append("token");
                stringBuffer.append(token);
                stringBuffer.append(interferenceCode);
                signature = Tools.encode(stringBuffer.toString());
                Log.i(TAG, "refundAllPost: " + token);
                Log.i(TAG, "refundAllPost: " + startTimeStamp / 1000 + "");
                Log.i(TAG, "refundAllPost: " + endTimeStamp / 1000 + "");
                Log.i(TAG, "refundAllPost: " + start);
                Log.i(TAG, "refundAllPost: " + count);
                Log.i(TAG, "refundAllPost: " + currentTimeStamp / 1000);
                Log.i(TAG, "refundAllPost: " + signature);

                //post
                retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                        .addConverterFactory(GsonConverterFactory.create()).build();//创建Retrofit并添加json转换器
                httpBinService = retrofit.create(HttpBinService.class);
                Call<RefundAllJson> call = httpBinService.refundAll(token,startTimeStamp / 1000 + "",
                        endTimeStamp / 1000 + "", start + "", count + "", currentTimeStamp / 1000 + "", signature);
                call.enqueue(new Callback<RefundAllJson>() {
                    @Override
                    public void onResponse(Call<RefundAllJson> call, Response<RefundAllJson> response) {
                        //取消正在查询的弹窗
                        dialog.cancel();
                        RefundAllJson refundAllJson = response.body();
                        Log.i(TAG, "onResponse: " + refundAllJson.getCode());
                        if (refundAllJson.getCode() ==0){
                            for (int i = 0; i < response.body().getData().getOilOrder().size(); i++)
                                list.add(response.body().getData().getOilOrder().get(i));
                            backAdapter.notifyDataSetChanged();
                        }else
                            Tools.codeError(getContext(), refundAllJson.getCode());
                    }

                    @Override
                    public void onFailure(Call<RefundAllJson> call, Throwable t) {
                        dialog.cancel();
                        Toast.makeText(getContext(),"连接失败",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void setButton (View view) {
        //功能按钮的点击事件
        btnPastHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimeStamp = new Date().getTime();
                startTimeStamp = endTimeStamp - 3600 * 1000;

                setEdit(startTimeStamp,endTimeStamp);
            }
        });

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeStamp = getTodayTimestamp();
                endTimeStamp = new Date().getTime();

                setEdit(startTimeStamp,endTimeStamp);
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeStamp = getWeekTimestamp();
                endTimeStamp = new Date().getTime();

                setEdit(startTimeStamp,endTimeStamp);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(view.getContext(),"","正在查询");
                refundAllPost();
            }
        });

    }

    //传入开始，结束时间戳，在editView上显示
    public void setEdit(long startTimeStamp, long endTimeStamp){
        String sStart = Tools.StampToTime(startTimeStamp);
        String sEnd = Tools.StampToTime(endTimeStamp);

        searStartTime.setText(sStart);//开始时间显示
        searEndTime.requestFocus();//输入焦点放在下一行

        searEndTime.setText(sEnd);//开始时间显示
        searEndTime.requestFocus();//输入焦点
        searEndTime.setSelection(searEndTime.getText().length());//输入焦点放在文字后
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//建立对话框对象

            View view = View.inflate(getActivity(),R.layout.data_time_picker,null);

            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.back_date_picker);
            final TimePicker timePicker = (TimePicker) view.findViewById(R.id.back_time_picker);//得到date/time picker实例

            builder.setView(view);//将组件显示在对话框上

            //运用Cal类
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),null);//初始化

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(Calendar.MINUTE);

            if (v.getId() == R.id.sear_startTime){

                final int inType = searStartTime.getInputType();
                searStartTime.setInputType(InputType.TYPE_NULL);
                searStartTime.onTouchEvent(event);
                searStartTime.setInputType(inType);
                searStartTime.setSelection(searStartTime.getText().length());

                builder.setTitle("选定开始时间");
                builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuffer sb1 = new StringBuffer();//sb对象
                        sb1.append(String.format("%d-%2d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));//填入格式化的年月日

                        sb1.append(" ");

                        sb1.append(timePicker.getCurrentHour()).append(":")
                                .append(timePicker.getCurrentMinute())
                                .append(":")
                                .append("00");//填入时分

                        startTimeStamp = Tools.TimeToStamp(sb1);//得到startTime的时间戳
//                        Log.i(TAG, "onClick: " + startTimeStamp);

                        searStartTime.setText(sb1);//开始时间显示
                        searEndTime.requestFocus();//输入焦点放在下一行

                        dialogInterface.cancel();//
                    }
                });
            } else if (v.getId() == R.id.sear_endTime){
                final int inType = searEndTime.getInputType();
                searEndTime.setInputType(InputType.TYPE_NULL);
                searEndTime.onTouchEvent(event);
                searEndTime.setInputType(inType);


                builder.setTitle("选定结束时间");
                builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuffer sb = new StringBuffer();//sb对象
                        sb.append(String.format("%d-%2d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));//填入格式化的年月日

                        sb.append(" ");

                        sb.append(timePicker.getCurrentHour()).append(":")
                                .append(timePicker.getCurrentMinute())
                                .append(":")
                                .append("00");//填入时分


                        endTimeStamp = Tools.TimeToStamp(sb);
                        Log.i(TAG, "onClick: " + endTimeStamp);
                        long s = new Date().getTime();
                        Log.i(TAG, "onClick1: " + s);
                        String string = Tools.StampToTime(endTimeStamp);
                        Log.i(TAG, "onClick2: " + string);


                        searEndTime.setText(sb);//开始时间显示
                        searEndTime.requestFocus();//输入焦点
                        searEndTime.setSelection(searEndTime.getText().length());//输入焦点放在文字后

                        dialogInterface.cancel();//
                    }
                });
            }

            Dialog dialog = builder.create();
            dialog.show();//显示dialog

        }
        return false;
    }
    public void initData() {
        //测试数据
        String sJson = "{\n" +
                "  \"code\": 0,\n" +
                "  \"message\": \"\",\n" +
                "  \"data\": {\n" +
                "    \"totalCount\": 2,\n" +
                "    \"current\": 1,\n" +
                "    \"oilOrder\": [\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"refundId\": \"xxxxx\",\n" +
                "        \"refundRequestTime\": \"xxxxx\",\n" +
                "        \"refundStatus\": 0,\n" +
                "        \"refundReason\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        refundAllJson = new Gson().fromJson(sJson,RefundAllJson.class);
    }
}