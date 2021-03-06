package com.cuse.myandroidpos;

import static android.content.ContentValues.TAG;
import static com.cuse.myandroidpos.TimeKey.getTodayTimestamp;
import static com.cuse.myandroidpos.TimeKey.getWeekTimestamp;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OilOrderList;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OrderSummaryJson;
import com.cuse.myandroidpos.databinding.CountItemContentBinding;
import com.cuse.myandroidpos.databinding.FragmentCountBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;


public class CountFragment extends Fragment implements View.OnTouchListener {
    private FragmentCountBinding binding;
    private DatePickerDialog datePickerDialog;
    private Button start_date;
    private Button end_date;
    private Button search_btn;
    private ArrayList<MyListData> search_ListData;
    private float all_money;
    private int order;
    private TextView sum_trac;
    private TextView sum_money;
    private OrderSummaryJson orderSummaryJson;
    private String sJson;
    private EditText searStartTime;
    private EditText searEndTime;
    private Button btnPastHour;
    private Button btnToday;
    private Button btnWeek;
    private Button btnSearch;

    private long currentTimeStamp;//当前时间戳
    private long startTimeStamp;
    private long endTimeStamp;
    private String signature;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCountBinding.inflate(inflater, container, false);
//        System.out.println("binding.getRoot() Search: " + binding.getRoot())
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCountButton(view);
        setCountJsonData();
        //recycleView,适配器单独写在了HomeAdapter
        RecyclerView recyclerView = binding.sumOilList;
        setRecyclerView(recyclerView);
    }

    //传入开始，结束时间戳，在editView上显示
    public void setEdit(long startTimeStamp, long endTimeStamp){
        String sStart = StampToTime(startTimeStamp);
        String sEnd = StampToTime(endTimeStamp);

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
                        sb.append(String.format("%d-%2d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));//填入格式化的年月日

                        sb.append(" ");

                        sb.append(timePicker.getCurrentHour()).append(":")
                                .append(timePicker.getCurrentMinute())
                                .append(":")
                                .append("00");//填入时分


                        endTimeStamp = TimeToStamp(sb);
                        Log.i(TAG, "onClick: " + endTimeStamp);
                        long s = new Date().getTime();
                        Log.i(TAG, "onClick1: " + s);
                        String string = StampToTime(endTimeStamp);
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
                "  \"code\": 0,\n" +
                "  \"message\": \"\",\n" +
                "  \"data\": {\n" +
                "    \"todayMoney\": 300.00,\n" +
                "    \"todayCount\": 2,\n" +
                "    \"size\": 2\n" +
                "    \"oilOrderList\": [\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },{\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilId\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxxxx\",\n" +
                "        \"oilCount\": 3,\n" +
                "        \"oilMoney\": 100.00\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

//        orderSummaryJson = new Gson().fromJson(sJson,OrderSummaryJson.class);
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
        CountAdapter countAdapter = new CountAdapter(oilOrderList,getActivity());
        recyclerView.setAdapter(countAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}