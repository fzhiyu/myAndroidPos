package com.cuse.myandroidpos;

import android.content.Context;
import android.widget.Toast;

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

    //显示code不为0时候的错误
    public static void codeError(Context context, int code) {
        switch (code) {
            case 1: {
                Toast.makeText(context, "时间戳错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 2: {
                Toast.makeText(context, "签名错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 3: {
                Toast.makeText(context, "参数错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 4: {
                Toast.makeText(context, "油站不存在", Toast.LENGTH_SHORT).show();
            }
            break;
            case 5: {
                Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 6: {
                Toast.makeText(context, "token错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 999: {
                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
}
