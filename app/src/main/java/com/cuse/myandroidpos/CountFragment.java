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

import com.cuse.myandroidpos.databinding.CountItemContentBinding;
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
    private TextView sum_trac;
    private TextView sum_money;
    private ArrayList<MyCountData> CountDataList;
    private MyCountData count_data;

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

        RecyclerView recyclerView = binding.sumOil;
        searchDate(view);
        search_btn = view.findViewById(R.id.count_search_btn);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRes();
                CountRes();
                setRes();
                setRecyclerView(recyclerView);
            }
        });
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(new ItemRecyclerViewAdapter(CountDataList));
    }

    @SuppressLint("SetTextI18n")
    public void setRes() {
        all_money = 0;
        order = 0;
        sum_trac = binding.getRoot().findViewById(R.id.sum_trac);
        sum_money = binding.getRoot().findViewById(R.id.sum_money_value);

        for (MyCountData m : CountDataList) {
            all_money += m.getMoney();
            order += m.getOrder();
        }

        sum_money.setText(String.valueOf(all_money));
        sum_trac.setText(String.valueOf(order));
    }

    public void CountRes() {
        CountDataList = new ArrayList<>();

        for (MyListData m : search_ListData) {
             if (CountDataList.size() == 0 || contains(m.getOil()) == -1) {
                 count_data = new MyCountData();
                 count_data.oil = m.getOil();
                 count_data.money = m.getFloatMoney();
                 count_data.order = 1;
                 CountDataList.add(count_data);
             } else {
                 float t = CountDataList.get(contains(m.getOil())).money;
                 int k = CountDataList.get(contains(m.getOil())).order;
                 CountDataList.get(contains(m.getOil())).setMoney(t + m.getFloatMoney());
                 CountDataList.get(contains(m.getOil())).setOrder(k + 1);
             }
        }
    }

    public int contains(String oil) {
        for (int i = 0; i < CountDataList.size(); i++) {
            if (oil.equals(CountDataList.get(i).getOil())) {
                return i;
            }
        }
        return -1;
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

    public static class ItemRecyclerViewAdapter
            extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
        private final ArrayList<MyCountData> mValues;

        ItemRecyclerViewAdapter(ArrayList<MyCountData> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CountItemContentBinding binding =
                    CountItemContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mOil.setText("oil: " + mValues.get(position).getOil() + " ");
            holder.mMoney.setText("money: " + mValues.get(position).getStringMoney() + " ");
            holder.mOrder.setText("order_num: " + mValues.get(position).getStringOrder());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mOil;
            private final TextView mMoney;
            private final TextView mOrder;

            ViewHolder(CountItemContentBinding binding) {
                super(binding.getRoot());
                mOil = binding.oilType;
                mMoney = binding.oilMoney;
                mOrder = binding.oilOrder;
            }
        }
    }

    public static class MyCountData {
        private String oil;
        private float money;
        private int order;

        public MyCountData(String oil, float money, int order) {
            this.oil = oil;
            this.money = money;
            this.order = order;
        }

        public MyCountData() {
        }

        public String getOil() {
            return oil;
        }

        public void setOil(String oil) {
            this.oil = oil;
        }

        public Float getMoney() {
            return money;
        }

        public String getStringMoney() {
            return String.valueOf(money);
        }

        public void setMoney(float money) {
            this.money = money;
        }

        public int getOrder() {
            return order;
        }

        public String getStringOrder() {
            return String.valueOf(order);
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

}