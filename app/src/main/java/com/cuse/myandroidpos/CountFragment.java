package com.cuse.myandroidpos;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.cuse.myandroidpos.databinding.FragmentCountBinding;
import com.cuse.myandroidpos.databinding.FragmentSearchBinding;
import com.cuse.myandroidpos.databinding.ItemListContentBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CountFragment extends Fragment {
    private FragmentCountBinding binding;
    private DatePickerDialog datePickerDialog;
    private Button start_date;
    private Button end_date;
    private Button search_btn;
    private SearchFragment.myDate current_start_date;
    private SearchFragment.myDate current_end_date;
    private SearchFragment.myDate select_Date;
    private ArrayList<MyListData> search_ListData;
    private float all_money;
    private int order;
    private Map<String, Float[]> map;
    private TextView sum_trac;
    private TextView sum_money;

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

        searchDate(view);

        search_btn = view.findViewById(R.id.count_search_btn);


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRes();
                CountRes();
                setRes();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setRes() {
        all_money = 0;
        order = 0;
        sum_trac = binding.getRoot().findViewById(R.id.sum_trac);
        sum_money = binding.getRoot().findViewById(R.id.sum_money_value);

        for (Float[] m : map.values()) {
            all_money += m[0];
            order += m[1];
        }
        sum_money.setText(String.valueOf(all_money));
        sum_trac.setText(String.valueOf(order));
    }

    public void  CountRes() {
        order = 0;
        map = new HashMap<>();
        Float[] fl = new Float[2];

        for (MyListData m : search_ListData) {

            if (map.isEmpty()) {
                fl[0] = m.getFloatMoney();
                fl[1] = 1f;
                map.put(m.getOil(), fl);
            } else if (map.containsKey(m.getOil())) {
                fl[0] = map.get(m.getOil())[0] + m.getFloatMoney();
                fl[1]++;
                map.put(m.getOil(), fl);
            } else {
                fl[0] = m.getFloatMoney();
                fl[1] = 1f;
                map.put(m.getOil(), fl);
            }
        }
    }

    private void searchDate(View view) {
        start_date = (Button) view.findViewById(R.id.start_date2);
        end_date = (Button) view.findViewById(R.id.end_date2);
        current_start_date = new SearchFragment.myDate();
        current_end_date = new SearchFragment.myDate();
        // perform click event on edit text
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                start_date.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                                current_start_date.setYear(year);
                                current_start_date.setMonth(monthOfYear + 1);
                                current_start_date.setDay(dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                end_date.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                                current_end_date.setYear(year);
                                current_end_date.setMonth(monthOfYear + 1);
                                current_end_date.setDay(dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void searchRes() {
        search_ListData = new ArrayList<>();
        ArrayList<MyListData> myListData = MyData.CreatData();
        Date start_date = new Date(current_start_date.getYear(), current_start_date.getMonth()
                , current_start_date.getDay());
        Date end_date = new Date(current_end_date.getYear(), current_end_date.getMonth(),
                current_end_date.getDay());

        for (MyListData m : myListData) {
            select_Date = new SearchFragment.myDate();
            splitDate(m.getOilOrderTime());
            Date sel = new Date(select_Date.getYear(), select_Date.getMonth(),
                    select_Date.getDay());
            int comparison1 = sel.compareTo(start_date);
            int comparison2 = end_date.compareTo(sel);
            if (comparison1 != -1 && comparison2 != -1) {
                search_ListData.add(m);
            }
        }
    }

    private void splitDate(String s) {
        String[] str = s.split("\\.");

        select_Date.setYear(Integer.parseInt(str[0]));
        select_Date.setMonth(Integer.parseInt(str[1]));
        select_Date.setDay(Integer.parseInt(str[2]));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}