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

import com.cuse.myandroidpos.databinding.FragmentSearchBinding;
import com.cuse.myandroidpos.databinding.ItemListContentBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private Button start_date;
    private Button end_date;
    private Button search_btn;
    private DatePickerDialog datePickerDialog;
    private myDate current_start_date;
    private myDate current_end_date;
    private myDate select_Date;
    private ArrayList<MyListData> search_ListData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
//        System.out.println("binding.getRoot() Search: " + binding.getRoot())
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.itemList2;

        View.OnClickListener onClickListener = itemView -> {
            MyListData item = (MyListData) itemView.getTag();
            Bundle argument = new Bundle();
            argument.putString(ItemDetailFragment.ARG_ITEM_ID, item.getOilOrderId());
            Navigation.findNavController(itemView).navigate(R.id.search_to_detail, argument);
        };

        searchDate(view);

        search_btn = view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRes();
                setupRecyclerView(recyclerView, onClickListener);
            }
        });
    }

    private void searchDate(View view) {
        start_date = (Button) view.findViewById(R.id.start_date);
        end_date = (Button) view.findViewById(R.id.end_date);
        current_start_date = new myDate();
        current_end_date = new myDate();
        // perform click event on edit text
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDate_picker(current_start_date, start_date);
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDate_picker(current_end_date, end_date);
            }
        });
    }

    public void SearchDate_picker(myDate current_date, Button date) {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        int mHour = c.get(Calendar.HOUR);
        int Minute = c.get(Calendar.MINUTE);
        int Second = c.get(Calendar.SECOND);
        System.out.println(c.getTime());

        // date picker dialog
        datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        date.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                        current_date.setYear(year);
                        current_date.setMonth(monthOfYear + 1);
                        current_date.setDay(dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void searchRes() {
        search_ListData = new ArrayList<>();
        ArrayList<MyListData> myListData = MyData.CreatData();
        Date start_date = new Date(current_start_date.getYear(), current_start_date.getMonth()
                                    , current_start_date.getDay());
        Date end_date = new Date(current_end_date.getYear(), current_end_date.getMonth(),
                current_end_date.getDay());

        for (MyListData m : myListData) {
            select_Date = new myDate();
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

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener
    ) {

        recyclerView.setAdapter(new ItemListFragment.SimpleItemRecyclerViewAdapter(
                search_ListData,
                onClickListener
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class myDate {
        private int day;
        private int month;
        private int year;

        public myDate(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public myDate() {
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}