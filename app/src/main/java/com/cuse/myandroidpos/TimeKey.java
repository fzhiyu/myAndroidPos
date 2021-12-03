package com.cuse.myandroidpos;

import java.util.Calendar;

//https://www.cnblogs.com/kiwifly/p/4840676.html
public class TimeKey {
    public static long getTimestampByOffsetDay(int day){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static long getTodayTimestamp(){

        return getTimestampByOffsetDay(0);

    }

    public static long getWeekTimestamp() {

        Calendar calendar = Calendar.getInstance();

        return getTimestampByOffsetDay(0 - calendar.get(Calendar.DAY_OF_WEEK) + 2);

    }

    public static long getMonthTimestamp() {

        Calendar calendar = Calendar.getInstance();

        return getTimestampByOffsetDay(0 - calendar.get(Calendar.DAY_OF_MONTH) + 1);

    }
}
