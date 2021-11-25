package com.cuse.myandroidpos;

import android.widget.TextView;

import java.util.ArrayList;


public class MyData {

    private static ArrayList<MyListData> myListData;

    public static ArrayList<MyListData> CreatData(){
        myListData = new ArrayList<>();

        myListData.add(new MyListData("1", "2021.1.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("2", "2021.2.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("3", "2021.3.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("4", "2021.4.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("5", "2021.5.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("6", "2021.6.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("7", "2021.7.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("8", "2021.8.1", "1234", 780, 13.0f));
        myListData.add(new MyListData("9", "2021.9.1", "1234", 780, 13.0f));

        return myListData;
    }
}
