package com.cuse.myandroidpos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
    public static String NoT(String time){
        return time.replace("T"," ");
    }

    //时间转成unix秒
    public static long TimeToStamp(StringBuffer sb){
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
    public static String StampToTime(long stamp){
        String s;

        SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s = df.format(new Date(stamp));

        return s;
    }
}
