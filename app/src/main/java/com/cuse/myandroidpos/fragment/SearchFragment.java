package com.cuse.myandroidpos.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.TimeKey;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderAllJson.OrderAllJson;
import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.adapter.HomeAdapter;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.databinding.FragmentSearchBinding;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchFragment extends Fragment implements View.OnTouchListener{
    private static final String TAG = "search";

    private FragmentSearchBinding binding;

    private EditText searStartTime;
    private EditText searEndTime;
    private Button btnPastHour;
    private Button btnToday;
    private Button btnWeek;
    private Button btnSearch;
    private RecyclerView recyclerView;

    private HttpBinService httpBinService;
    private Retrofit retrofit;

    private Dialog dialog;

    private long startTimeStamp;
    private long endTimeStamp;
    private String sStart;
    private String sEnd;
    private int start = 1;
    private int count = 200;
    private String interferenceCode = "24bD5w1af2bC616fc677cAe6If44F3q5";
    private String token;

    private List<OilOrderList> oilOrderLists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        token = ((MainActivity)getActivity()).getToken();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.searchItemList;
        //文字框
        searStartTime = view.findViewById(R.id.search_searStartTime);
        searEndTime = view.findViewById(R.id.search_searEndTime);
        searStartTime.setOnTouchListener(this);
        searEndTime.setOnTouchListener(this);
        //功能按钮
        btnPastHour = view.findViewById(R.id.search_past_hour);
        btnToday = view.findViewById(R.id.search_today);
        btnWeek = view.findViewById(R.id.search_week);
        btnSearch = view.findViewById(R.id.search_btn_search);
        //retrofit
        retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        httpBinService = retrofit.create(HttpBinService.class);

        //按钮的点击事件
        setButton(view);
    }

    public void setRecyclerView() {
        //设置竖直
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //将数据填入homeAdapt
        HomeAdapter homeAdapter = new HomeAdapter(oilOrderLists,0 , getActivity());
        recyclerView.setAdapter(homeAdapter);
        //设置点击事件，点击事件的接口定义哎HomeAdapter
        homeAdapter.setHomeRecyclerItemClickListener(new HomeAdapter.OnHomeRecyclerItemClickListener() {
            @Override
            public void OnHomeRecyclerItemClick(int position) {
                Bundle bundle = new Bundle();
                //用bundle来传输对象（传输的是OrderLastJson包里面的OilOrderList对象），OilOrderList类需要implements Serializable
                // 就可以把bundle.putString换成，bundle.putSerializable
                //详情界面可以用oilOrder = (OilOrderList) bundle.getSerializable("LastOilOrder");来得到对象
                bundle.putSerializable("LastOilOrder",oilOrderLists.get(position));

                Navigation.findNavController(getView()).navigate(R.id.search_to_detail,bundle);
            }
        });
    }

    //获取搜索订单
    public void postOrderAll() {
        //显示正在查询的弹窗
        dialog = ProgressDialog.show(getContext(),"","正在查询");

        long timeStamp = new Date().getTime();
        //得到字符串并加密编码
        String stringBuffer = "count" +
                 count +
                "endTime" +
                sEnd +
                "start" +
                start +
                "startTime" +
                sStart +
                "timestamp" +
                timeStamp / 1000 +
                "token" +
                token +
                interferenceCode;
        String signature = Tools.md5.md5(stringBuffer);
        Log.i(TAG, "postOrderAll: " + stringBuffer);
        Log.i(TAG, "postOrderAll: " + signature);

        Call<OrderAllJson> call = httpBinService.orderAll(token
                , sStart
                , sEnd
                , start + ""
                , count + ""
                , timeStamp/1000 + ""
                , signature);
        call.enqueue(new Callback<OrderAllJson>() {
            @Override
            public void onResponse(Call<OrderAllJson> call, Response<OrderAllJson> response) {
                //取消正在查询的弹窗
                dialog.cancel();
                OrderAllJson orderAllJson = response.body();

                //测试，用完删除
                Gson gson = new Gson();
                String s = gson.toJson(orderAllJson);
                Log.i(TAG, "response.code: " + response.code());
                Log.i(TAG, "response.json: " + s);

                if (response.isSuccessful() && orderAllJson != null) {
                    if (orderAllJson.getCode() == 0) {
                        if (orderAllJson.getData().getOilOrder().size() == 0)
                            Toast.makeText(getContext(), "无订单", Toast.LENGTH_SHORT).show();
                        else {
                            oilOrderLists = orderAllJson.getData().getOilOrder();
                            setRecyclerView();
                        }

                    } else
                        Tools.codeError(getContext(), orderAllJson.getCode());
                }
            }
            @Override
            public void onFailure(Call<OrderAllJson> call, Throwable t) {
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
                startTimeStamp = TimeKey.getTodayTimestamp();
                endTimeStamp = new Date().getTime();

                setEdit(startTimeStamp,endTimeStamp);
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeStamp = TimeKey.getWeekTimestamp();
                endTimeStamp = new Date().getTime();

                setEdit(startTimeStamp,endTimeStamp);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searStartTime.getText() == null || searStartTime.getText().toString().equals("")){
                    Toast.makeText(view.getContext(),"开始时间不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (searEndTime.getText() == null || searEndTime.getText().toString().equals("")){
                    Toast.makeText(view.getContext(),"结束时间不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    postOrderAll();
                }
            }
        });
    }

    //传入开始，结束时间戳，在editView上显示
    public void setEdit(long startTimeStamp, long endTimeStamp){
        sStart = Tools.StampToTime(startTimeStamp);
        sEnd = Tools.StampToTime(endTimeStamp);

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

            if (v.getId() == R.id.search_searStartTime){

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

                        startTimeStamp = Tools.TimeToStamp(sb1);//得到startTime的时间戳
//                        Log.i(TAG, "onClick: " + startTimeStamp);

                        searStartTime.setText(sb1);//开始时间显示
                        searEndTime.requestFocus();//输入焦点放在下一行

                        dialogInterface.cancel();//
                    }
                });
            } else if (v.getId() == R.id.search_searEndTime){
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


                        endTimeStamp = Tools.TimeToStamp(sb);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}