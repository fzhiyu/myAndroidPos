package com.cuse.myandroidpos;

import android.widget.TextView;

import java.util.ArrayList;


public class MyData {

    private static ArrayList<MyListData> myListData;

    public static ArrayList<MyListData> CreatData(){
        myListData = new ArrayList<>();

        myListData.add(new MyListData("1", "0324", "1234", 780, 13.0f));
        myListData.add(new MyListData("2", "0325", "1234", 780, 13.0f));

        return myListData;
    }
}
