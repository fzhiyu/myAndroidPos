package com.cuse.myandroidpos;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cuse.myandroidpos.databinding.FragmentBackBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BackFragment extends Fragment implements View.OnTouchListener{

    private FragmentBackBinding binding;
    private EditText searStartTime;
    private EditText searEndTime;
    private long currentTimeStamp;//当前时间戳
    private long startTimeStamp;
    private long endTimeStamp;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

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

        searStartTime = view.findViewById(R.id.sear_startTime);
        searEndTime = view.findViewById(R.id.sear_endTime);
        searStartTime.setOnTouchListener(this);
        searEndTime.setOnTouchListener(this);

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
            //https://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date1 = df.parse(sb.toString());
            Log.i(TAG, "onC4: " + date1.toString());
            unixTimestamp = date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTimestamp;
    }
    //unix秒转化为"YYYY-MM-dd HH:mm:ss"格式字符串
    public String StampToTime(long stamp){
        String s;

        SimpleDateFormat df =new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        s = df.format(new Date(stamp));

        return s;
    }

    public void OnPastHourClick(View view){

    }

    public void OnTodayClick(View view){

    }

    public void OnWeekClick(View view){

    }

}