package com.cuse.myandroidpos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.Post.LoginJson.LoginRequest;
import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastRequest;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemListFragment extends Fragment {

    private FragmentItemListBinding binding;
    private long currentTimeStamp;//订单刷新时间
    private OrderLastJson orderLastJson;//储存得到的订单数据

    private int internet;//网络连接参量，0代表连上，1代表断开

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

    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新控件

    private HttpBinService httpBinService;
    private Retrofit retrofit;

    //请求参数
    private long timeStamp;
    private String signature;
    private String staionId;

    private String sJson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        //binding -> app:id/item_list_container -> fragment_item_list
//        System.out.println("binding.getRoot(): " + binding.getRoot());
        //orderLastPost(getLastRequestJson());//先请求数据
        //测试数据
        test();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ////网络不好时弹出未连接网络的框
        setInternetLayout();



        //设置显示总金钱和总订单
        tvTotalMoney = view.findViewById(R.id.tv_home_TodayTotalMoney);
        tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() + "");
        tvTotalOrder = view.findViewById(R.id.tv_home_TodayTotalOrder);
        tvTotalOrder.setText(orderLastJson.getData().getTodayCount() + "");

        //按钮功能
        setBackButton(view);
        //下拉刷新
        swipeRefreshLayout = view.findViewById(R.id.swipe_home);
        handleDownPullUpdate();

        //recycleView,适配器单独写在了HomeAdapter
        RecyclerView recyclerView = binding.itemList;
        setRecyclerView(recyclerView);
    }


    public void setBackButton (View view) {

        //找到按钮实例,搜索，汇总，退单，设置
        btnSearch = view.findViewById(R.id.btn_home_search);
        btnAll = view.findViewById(R.id.btn_home_all);
        btnRefund = view.findViewById(R.id.btn_home_refund);
        btnSet = view.findViewById(R.id.btn_home_set);

        //搜索按钮
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_search, null);
            }
        });

        //汇总按钮
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_count, null);
            }
        });

        //退单按钮
        btnRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_back, null);
            }
        });

        //设置按钮
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_setting, null);
            }
        });

    }

    public void setRecyclerView(RecyclerView recyclerView) {

        //设置竖直
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //将数据填入homeAdapt
        HomeAdapter homeAdapter = new HomeAdapter(orderLastJson.getData().getOilOrderList(),getActivity());
        recyclerView.setAdapter(homeAdapter);
        //设置点击事件，点击事件的接口定义哎HomeAdapter
        homeAdapter.setHomeRecyclerItemClickListener(new HomeAdapter.OnHomeRecyclerItemClickListener() {
            @Override
            public void OnHomeRecyclerItemClick(int position) {
                Bundle bundle = new Bundle();

                //用bundle来传输对象（传输的是OrderLastJson包里面的OilOrderList对象），OilOrderList类需要implements Serializable
                // 就可以把bundle.putString换成，bundle.putSerializable
                //详情界面可以用oilOrder = (OilOrderList) bundle.getSerializable("LastOilOrder");来得到对象
                int number = orderLastJson.getData().getOilOrderList().size() - position -1;
                bundle.putSerializable("LastOilOrder",orderLastJson.getData().getOilOrderList().get(number));

                Navigation.findNavController(getView()).navigate(R.id.show_item_detail,bundle);
            }
        });
    }

    //下拉刷新
    private void handleDownPullUpdate(){
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        orderLastPost(getLastRequestJson());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });
    }

    public void orderLastPost(String route){
        retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        httpBinService = retrofit.create(HttpBinService.class);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), route);
        Call<OrderLastJson> call = httpBinService.orderLast(body);
        call.enqueue(new Callback<OrderLastJson>() {
            @Override
            public void onResponse(Call<OrderLastJson> call, Response<OrderLastJson> response) {
                if (response.isSuccessful())
                    orderLastJson = response.body();
            }

            @Override
            public void onFailure(Call<OrderLastJson> call, Throwable t) {
                Toast.makeText(getContext(),"连接失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getLastRequestJson(){
        timeStamp = new Date().getTime();
        //得到字符串并加密编码
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("staionId");
        stringBuffer.append(staionId);
        stringBuffer.append("timestamp");
        stringBuffer.append(timeStamp / 1000);
        stringBuffer.append(LoginActivity.interferenceCode);
        signature = MD5AndBase64.md5(stringBuffer.toString());
        //得到提交的json数据 route
        OrderLastRequest orderLastRequest = new OrderLastRequest();
        orderLastRequest.setStationId(staionId);
        orderLastRequest.setTimestamp(timeStamp / 1000 + "");
        orderLastRequest.setSignature(signature);

        Gson gson = new Gson();
        String route = gson.toJson(orderLastRequest);

        return route;
    }

    //网络不好时弹出未连接网络的框
    public void setInternetLayout(){
        internet = 1;
        if (internet == 1){
            TextView internet = getView().findViewById(R.id.tv_home_internet);
            internet.setVisibility(View.VISIBLE);
        }
    }

    public void test() {
        //测试的数据，不用管，但是需要写在前面，不然会出现bug
        sJson = "{\n" +
                "  \"code\": 0,\n" +
                "  \"message\": \"\",\n" +
                "  \"data\": {\n" +
                "    \"stationName\": \"XXXX\",\n" +
                "    \"todayMoney\": 300.00,\n" +
                "    \"todayCount\": 2,\n" +
                "    \"oilOrderList\": [\n" +
                "      {\n" +
                "        \"oilOrderId\": \"1\",\n" +
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
                "        \"oilOrderId\": \"2\",\n" +
                "        \"oilOrderTime\": \"xxxxx\",\n" +
                "        \"oilName\": \"xxx\",\n" +
                "        \"user\": \"xxxxxxx\",\n" +
                "        \"money\": 200.00,\n" +
                "        \"discount\": 2.00,\n" +
                "        \"coupon\": 3.00,\n" +
                "        \"balance\": 0,\n" +
                "        \"cash\": 95.00\n" +
                "      },{\n" +
                "        \"oilOrderId\": \"3\",\n" +
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
                "        \"oilOrderId\": \"4\",\n" +
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
                "        \"oilOrderId\": \"5\",\n" +
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
                "        \"oilOrderId\": \"6\",\n" +
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
                "        \"oilOrderId\": \"7\",\n" +
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
                "        \"oilOrderId\": \"8\",\n" +
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
                "        \"oilOrderId\": \"9\",\n" +
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
                "        \"oilOrderId\": \"10\",\n" +
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
                "        \"oilOrderId\": \"11\",\n" +
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
                "        \"oilOrderId\": \"12\",\n" +
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
                "        \"oilOrderId\": \"13\",\n" +
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
                "        \"oilOrderId\": \"14\",\n" +
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
                "        \"oilOrderId\": \"15\",\n" +
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
                "        \"oilOrderId\": \"16\",\n" +
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

        orderLastJson = new Gson().fromJson(sJson,OrderLastJson.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}