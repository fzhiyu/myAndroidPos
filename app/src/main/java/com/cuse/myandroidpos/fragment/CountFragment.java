package com.cuse.myandroidpos.fragment;

import static com.cuse.myandroidpos.TimeKey.getTodayTimestamp;
import static com.cuse.myandroidpos.TimeKey.getWeekTimestamp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.adapter.CountAdapter;
import com.cuse.myandroidpos.MyListData;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OilOrderList;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OrderSummaryJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.databinding.FragmentCountBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cuse.myandroidpos.utils.SunmiPrintHelper;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import top.androidman.SuperButton;


public class CountFragment extends Fragment implements View.OnTouchListener {

    private static final String TAG = "count";
    private FragmentCountBinding binding;
    private DatePickerDialog datePickerDialog;
    private Button start_date;
    private Button end_date;
    private Button search_btn;
    private ArrayList<MyListData> search_ListData;
    private float all_money;
    private int order;
    private TextView sum_orderNumber;
    private TextView sum_money;
    private OrderSummaryJson orderSummaryJson;
    private String sJson;
    private EditText searStartTime;
    private EditText searEndTime;
    private SuperButton btnPastHour;
    private SuperButton btnToday;
    private SuperButton btnWeek;
    private Button btnSearch;

    private long currentTimeStamp;//当前时间戳
    private long startTimeStamp;
    private long endTimeStamp;
    private String signature;
    private String sStart;
    private String sEnd;

    private List<OilOrderList> oilCountLists;
    private String token;
//    private long timeStamp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCountBinding.inflate(inflater, container, false);
//        System.out.println("binding.getRoot() Search: " + binding.getRoot())
        token = getArguments().getString("token");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sum_money = view.findViewById(R.id.tv_sum_totalMoney);
        sum_orderNumber = view.findViewById(R.id.tv_sum_totalOrderNumber);

        setCountButton(view);
        setCountJsonData();

        //搜索汇总
        searchSummary(view);
        //recycleView,适配器单独写在了HomeAdapter
        RecyclerView recyclerView = binding.sumOilList;
        setRecyclerView(recyclerView);
        //打印汇总小票
        countPrint(view);
    }

    //搜索汇总
    private void searchSummary(View view) {
        Button btn_search = view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postSummary();
            }
        });
    }

    //post 汇总结果
    private void postSummary() {
        // on below line we are creating a retrofit builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();
        HttpBinService httpBinService = retrofit.create(HttpBinService.class);

        //存储数据
        oilCountLists = new ArrayList<>();

        long timeStamp = new Date().getTime();
        //得到字符串并加密编码
        String stringBuffer =
                "endTime" +
                sEnd +
                "startTime" +
                sStart +
                "timestamp" +
                timeStamp / 1000 +
                "token" +
                token +
                LoginActivity.interferenceCode;
        String signature = Tools.md5.md5(stringBuffer);

        Call<OrderSummaryJson> call = httpBinService.orderSummary(token
                , sStart + ""
                , sEnd + ""
                , timeStamp/1000 + ""
                , signature);

        call.enqueue(new Callback<OrderSummaryJson>() {
            @Override
            public void onResponse(Call<OrderSummaryJson> call, Response<OrderSummaryJson> response) {
                orderSummaryJson = response.body();
//                //测试，用完删除
//                Gson gson = new Gson();
//                String s = gson.toJson(orderSummaryJson);
                Log.i(TAG, "stringBuffer: " + stringBuffer);
//                Log.i(TAG, "signature: " + signature);
//                Log.i(TAG, "response.code: " + response.code());
//                Log.i(TAG, "orderSummaryJson: " + s);

                if(orderSummaryJson == null) {
                    Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
                } else if (orderSummaryJson.getCode() == 0) {
                    sum_money.setText("加油总额：" + orderSummaryJson.getData().getTodayMoney());
                    sum_orderNumber.setText("订单数：" + orderSummaryJson.getData().getTodayCount());
                    oilCountLists.addAll(orderSummaryJson.getData().getOilOrderList());
                } else {
                    Tools.codeError(getContext(), orderSummaryJson.getCode());
                }
//                Log.i("getOilOrder", "" + orderSummaryJson.getData().getOilOrderList());
//                Log.i("oilOrderLists", "" + oilCountLists);
                RecyclerView recyclerView = binding.sumOilList;
                setRecyclerView(recyclerView);
            }

            @Override
            public void onFailure(Call<OrderSummaryJson> call, Throwable t) {

            }
        });
    }

    //打印汇总小票
    public void countPrint(View view) {
        Button btn_count_print = view.findViewById(R.id.btn_sum_print);
        btn_count_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderSummaryJson != null && oilCountLists != null) {
                    long timeStamp = new Date().getTime();
                    StringBuilder content = new StringBuilder();
                    content.append("油站名称:" + "北京测试公司测试站" + "\n");
                    content.append("当前时间:").append(Tools.StampToTime(timeStamp)).append("\n");
                    content.append("开始时间:").append(sStart).append("\n");
                    content.append("结束时间:").append(sEnd).append("\n");
                    content.append("分油品的加油金额和笔数").append("\n");
                    content.append("总加油金额:").append(orderSummaryJson.getData().getTodayMoney())
                            .append("\n");
                    content.append("总笔数:").append(orderSummaryJson.getData().getTodayCount())
                            .append("\n");
                    for (OilOrderList t : oilCountLists) {
                        content.append(t.getOilName()).append("  ").append("加油金额:")
                                .append(t.getOilMoney()).append("  ").append("笔数:")
                                .append(t.getOilCount()).append("\n");
                    }
                    Log.e(TAG, "onClick: " + content);

                    float size = 24;
                    String testFont = null;
                    boolean isBold = true;
                    boolean isUnderLine = true;
                    SunmiPrintHelper.getInstance().printText(content.toString(), size
                            , isBold, isUnderLine, testFont);
                    SunmiPrintHelper.getInstance().feedPaper();
                }
            }
        });
    }
    //传入开始，结束时间戳，在editView上显示
    public void setEdit(long startTimeStamp, long endTimeStamp){
        sStart = StampToTime(startTimeStamp);
        sEnd = StampToTime(endTimeStamp);
        Log.e(TAG, "setEdit: " + sEnd);

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

            View view = View.inflate(getActivity(), R.layout.data_time_picker,null);

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
                        sb1.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));//填入格式化的年月日

                        sb1.append(" ");

                        sb1.append(timePicker.getCurrentHour()).append(":")
                                .append(timePicker.getCurrentMinute())
                                .append(":")
                                .append("00");//填入时分

                        Log.e(TAG, "onClick: " + sb1);
                        startTimeStamp = TimeToStamp(sb1);//得到startTime的时间戳
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
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));//填入格式化的年月日

                        sb.append(" ");

                        sb.append(timePicker.getCurrentHour()).append(":")
                                .append(timePicker.getCurrentMinute())
                                .append(":")
                                .append("00");//填入时分


                        endTimeStamp = TimeToStamp(sb);
//                        Log.i(TAG, "onClick: " + endTimeStamp);
                        long s = new Date().getTime();
//                        Log.i(TAG, "onClick1: " + s);
                        String string = StampToTime(endTimeStamp);
//                        Log.i(TAG, "onClick2: " + string);


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

    //时间转成unix秒
    public long TimeToStamp(StringBuffer sb){
        long unixTimestamp = 0l;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = df.parse(sb.toString());
            unixTimestamp = date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTimestamp;
    }
    //unix秒转化为"yyyy-MM-dd HH:mm:ss"格式字符串
    public String StampToTime(long stamp){
        String s;

        SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s = df.format(new Date(stamp));

        return s;
    }

    public void setCountButton (View view) {
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
    }

    public void setCountJsonData () {
        //测试的数据，不用管，但是需要写在前面，不然会出现bug
        orderSummaryJson = new OrderSummaryJson();
        sJson = "{\n" +
                "\t\"code\": 0,\n" +
                "\t\"message\": \"\",\n" +
                "\t\"data\": {\n" +
                "\t\t\"todayMoney\": 300.00,\n" +
                "\t\t\"todayCount\": 2,\n" +
                "        \"size\": 1,\n" +
                "        \"oilOrderList\": [{\n" +
                "\t\t\t\"oilId\": \"xxxxx\",\n" +
                "\t\t\t\"oilName\": \"xxxxx\",\n" +
                "\t\t\t\"oilCount\": 1,\n" +
                "\t\t\t\"oilMoney\": 100.00\n" +
                "},\n" +
                "{\n" +
                "\t\t\t\"oilId\": \"xxxxx\",\n" +
                "\t\t\t\"oilName\": \"xxxxx\",\n" +
                "\t\t\t\"oilCount\": 1,\n" +
                "\t\t\t\"oilMoney\": 100.00\n" +
                "}\n" +
                "]}\n" +
                "}\n" ;

        orderSummaryJson = new Gson().fromJson(sJson,OrderSummaryJson.class);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        //设置竖直
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //将数据填入homeAdapt
        List<OilOrderList> oilOrderList = new ArrayList<>();
        OilOrderList list1 = new OilOrderList();
        list1.setOilMoney(101);
        list1.setOilCount(1);
        list1.setOilId("1");
        list1.setOilName("ds");
        oilOrderList.add(list1);
        CountAdapter countAdapter = new CountAdapter(oilCountLists,getActivity());
        recyclerView.setAdapter(countAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}