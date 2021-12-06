package com.cuse.myandroidpos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;
import com.cuse.myandroidpos.databinding.FragmentHomeItemBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ItemListFragment extends Fragment {

    private FragmentItemListBinding binding;

    private TextView tvOilOrderId;
    private TextView tvOil;
    private TextView tvMoney;
    private TextView tvOilOrderTime;
    private TextView tvUser;

    private TextView tvTotalMoney;
    private TextView tvTotalOrder;

    private Button btnSearch;
    private Button btnAll;
    private Button btnRefund;
    private Button btnSet;

    private OrderLastJson orderLastJson;

    private String sJson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        //binding -> app:id/item_list_container -> fragment_item_list
//        System.out.println("binding.getRoot(): " + binding.getRoot());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //设置显示总金钱和总订单
        tvTotalMoney = view.findViewById(R.id.tv_home_TodayTotalMoney);
        tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() +"");
        tvTotalOrder = view.findViewById(R.id.tv_home_TodayTotalOrder);
        tvTotalOrder.setText(orderLastJson.getData().getTodayCount());

        //找到按钮实例
        btnSearch = view.findViewById(R.id.btn_home_search);
        btnAll = view.findViewById(R.id.btn_home_all);
        btnRefund = view.findViewById(R.id.btn_home_refund);
        btnSet = view.findViewById(R.id.btn_home_set);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_search, null);
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_count, null);
            }
        });

        btnRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_back, null);
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_setting, null);
            }
        });

        RecyclerView recyclerView = binding.itemList;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        HomeAdapter homeAdapter = new HomeAdapter(orderLastJson.getData().getOilOrderList(),getActivity());
        recyclerView.setAdapter(homeAdapter);


        orderLastJson = new Gson().fromJson(sJson,OrderLastJson.class);

        sJson = "{\n" +
                "  \"code\": 0,\n" +
                "  \"message\": \"\",\n" +
                "  \"data\": {\n" +
                "    \"stationName\": \"XXXX\",\n" +
                "    \"todayMoney\": 300.00,\n" +
                "    \"todayCount\": 2,\n" +
                "    \"oilOrderList\": [\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },{\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },\n" +
                "      {\n" +
                "        \"oilOrderId\": \"xxxxx\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 100.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }







}