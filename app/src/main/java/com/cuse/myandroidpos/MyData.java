package com.cuse.myandroidpos;

import android.widget.TextView;

import java.util.ArrayList;


public class MyData {

    private static ArrayList<MyListData> myListData;

    public static ArrayList<MyListData> CreatData(){
        myListData = new ArrayList<>();

        myListData.add(new MyListData("1", "2021.7.1", "1234", 780, 10.0f));
        myListData.add(new MyListData("2", "2021.8.1", "1234", 790, 10.0f));
        myListData.add(new MyListData("3", "2021.9.1", "1234", 770, 10.0f));
        myListData.add(new MyListData("4", "2021.10.1", "1234", 780, 10.0f));
        myListData.add(new MyListData("5", "2021.11.1", "1234", 770, 10.0f));

        return myListData;
    }
}
