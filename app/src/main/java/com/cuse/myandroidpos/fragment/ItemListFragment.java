package com.cuse.myandroidpos.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cuse.myandroidpos.adapter.HomeAdapter;
import com.cuse.myandroidpos.ListDataSave;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.MD5AndBase64;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemListFragment extends Fragment {

    private FragmentItemListBinding binding;
    private long currentTimeStamp;//订单刷新时间
    //private OrderLastJson orderLastJson;//储存得到的订单数据

    private List<OilOrderList> oilOrderLists;
    private HomeAdapter homeAdapter;

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

    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新控件

    private HttpBinService httpBinService;
    private Retrofit retrofit;

    //请求参数
    private String stationId;
    String token = "test123";

    private Context mContext;
    private ListDataSave dataSave;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);

        mContext = getContext();


//        oilOrderLists = new ArrayList<>();
//        OilOrderList oilOrderList = new OilOrderList("2022-01-03T14:22:17");
//        oilOrderLists.add(oilOrderList);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //总金钱和总订单数
        tvTotalMoney = view.findViewById(R.id.tv_home_TodayTotalMoney);
        tvTotalOrder = view.findViewById(R.id.tv_home_TodayTotalOrder);
        //下拉刷新
        swipeRefreshLayout = view.findViewById(R.id.swipe_home);
        //列表
        recyclerView = binding.itemList;
        //找到按钮实例,搜索，汇总，退单，设置
        btnSearch = view.findViewById(R.id.btn_home_search);
        btnAll = view.findViewById(R.id.btn_home_all);
        btnRefund = view.findViewById(R.id.btn_home_refund);
        btnSet = view.findViewById(R.id.btn_home_set);

        //创建retrofit实例b
        // on below line we are creating a retrofit builder and passing our base url
        retrofit = new Retrofit.Builder().baseUrl("https://paas.u-coupon.cn/pos_api/v1/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();

        // below line is to create an instance for our retrofit api class.
        httpBinService = retrofit.create(HttpBinService.class);


        oilOrderLists = new ArrayList<>(20);

        //recycleView,适配器单独写在了HomeAdapter
        setRecyclerView(recyclerView);

        //网络不好时弹出未连接网络的框
        setInternetLayout();

        //按钮功能
        setBackButton(view);
        //下拉刷新
        handleDownPullUpdate();
    }

    public void setBackButton (View view) {
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
        //每个item的高度一定
        recyclerView.setHasFixedSize(true);
        initData();

        //将数据填入homeAdapt
        homeAdapter = new HomeAdapter(oilOrderLists,getActivity());
        recyclerView.setAdapter(homeAdapter);
        //默认添加动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //设置点击事件，点击事件的接口定义HomeAdapter
        homeAdapter.setHomeRecyclerItemClickListener(new HomeAdapter.OnHomeRecyclerItemClickListener() {
            @Override
            public void OnHomeRecyclerItemClick(int position) {
                Bundle bundle = new Bundle();
                //用bundle来传输对象（传输的是OrderLastJson包里面的OilOrderList对象），OilOrderList类需要implements Serializable
                // 就可以把bundle.putString换成，bundle.putSerializable
                //详情界面可以用oilOrder = (OilOrderList) bundle.getSerializable("LastOilOrder");来得到对象
                bundle.putSerializable("LastOilOrder",oilOrderLists.get(position));

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
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                },3000);
                orderLastPost();
            }
        });
    }

    public void orderLastPost(){
        long timeStamp = new Date().getTime();
        //得到字符串并加密编码
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("timestamp");
        stringBuffer.append(timeStamp / 1000);
        stringBuffer.append("token");
        stringBuffer.append(token);
        stringBuffer.append(LoginActivity.interferenceCode);
        String signature = MD5AndBase64.md5(stringBuffer.toString());
        //String signature = Tools.encode(stringBuffer.toString());
        Log.i("hejun", "orderLastPost: " + timeStamp / 1000);
        Log.i("hejun", "orderLastPost: " + stringBuffer.toString());
        Log.i("hejun", "orderLastPost: " + signature);

        //使用Retrofit进行post
        Call<OrderLastJson> call = httpBinService.orderLast(token,timeStamp / 1000 + "", signature);
        call.enqueue(new Callback<OrderLastJson>() {
            @Override
            public void onResponse(Call<OrderLastJson> call, Response<OrderLastJson> response) {
                swipeRefreshLayout.setRefreshing(false);
                OrderLastJson orderLastJson = response.body();
                //
                Log.i("hejun", "onResponse: " + response.body());
                Gson gson = new Gson();
                String s = gson.toJson(orderLastJson);
                Log.i("hejun", "onResponse: " + s);

                if (response.body().getCode() == 0) {
                    //设置显示总金钱和总订单
                    Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                    tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() + "");
                    tvTotalOrder.setText(orderLastJson.getData().getTodayCount() + "");
                    for (int i = 0; i < response.body().getData().getOilOrderList().size(); i++) {
                        Log.i("hejun", "onResponse: " + orderLastJson.getData().getOilOrderList().get(i).compareTo(oilOrderLists.get(0)));
                        if (oilOrderLists.get(0).compareTo(orderLastJson.getData().getOilOrderList().get(i)) >= 0) {
                            oilOrderLists.add(0, response.body().getData().getOilOrderList().get(i));
                        }
                    }

                    homeAdapter.notifyDataSetChanged();
                } else
                    Tools.codeError(getContext(), orderLastJson.getCode());

            }
            @Override
            public void onFailure(Call<OrderLastJson> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(),"连接失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //网络不好时弹出未连接网络的框
    public void setInternetLayout(){
        internet = 1;
        if (internet == 1){
            TextView internet = getView().findViewById(R.id.tv_home_internet);
            internet.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        String sJson = "{\n" +
                "\t\"code\": 0,\n" +
                "\t\"data\": {\n" +
                "\t\t\"stationName\": \"北京测试公司测试站 \",\n" +
                "\t\t\"todayCount\": 610,\n" +
                "\t\t\"oilOrderList\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:52:25\",\n" +
                "\t\t\t\t\"money\": 300,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"4077192745765\",\n" +
                "\t\t\t\t\"discount\": 3,\n" +
                "\t\t\t\t\"oilName\": \"汽油95号\",\n" +
                "\t\t\t\t\"user\": \"T9999994077\",\n" +
                "\t\t\t\t\"cash\": 297\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:47:48\",\n" +
                "\t\t\t\t\"money\": 256,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"9517192468202\",\n" +
                "\t\t\t\t\"discount\": 12.8,\n" +
                "\t\t\t\t\"oilName\": \"汽油98号\",\n" +
                "\t\t\t\t\"user\": \"T9999999517\",\n" +
                "\t\t\t\t\"cash\": 243.2\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:47:12\",\n" +
                "\t\t\t\t\"money\": 265,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"3823192432156\",\n" +
                "\t\t\t\t\"discount\": 10.6,\n" +
                "\t\t\t\t\"oilName\": \"柴油-20号\",\n" +
                "\t\t\t\t\"user\": \"T9999993823\",\n" +
                "\t\t\t\t\"cash\": 254.4\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:44:36\",\n" +
                "\t\t\t\t\"money\": 243,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"3203192276095\",\n" +
                "\t\t\t\t\"discount\": 9.72,\n" +
                "\t\t\t\t\"oilName\": \"柴油-20号\",\n" +
                "\t\t\t\t\"user\": \"T9999993203\",\n" +
                "\t\t\t\t\"cash\": 233.28\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:41:58\",\n" +
                "\t\t\t\t\"money\": 230,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"8175192118713\",\n" +
                "\t\t\t\t\"discount\": 0,\n" +
                "\t\t\t\t\"oilName\": \"柴油-35号\",\n" +
                "\t\t\t\t\"user\": \"T9999998175\",\n" +
                "\t\t\t\t\"cash\": 230\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:41:47\",\n" +
                "\t\t\t\t\"money\": 235,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"4050192107691\",\n" +
                "\t\t\t\t\"discount\": 2.35,\n" +
                "\t\t\t\t\"oilName\": \"柴油-10号\",\n" +
                "\t\t\t\t\"user\": \"T9999994050\",\n" +
                "\t\t\t\t\"cash\": 232.65\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:38:04\",\n" +
                "\t\t\t\t\"money\": 231,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"4190191884146\",\n" +
                "\t\t\t\t\"discount\": 2.31,\n" +
                "\t\t\t\t\"oilName\": \"汽油98号\",\n" +
                "\t\t\t\t\"user\": \"T9999994190\",\n" +
                "\t\t\t\t\"cash\": 228.69\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:37:35\",\n" +
                "\t\t\t\t\"money\": 270,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"8754191855074\",\n" +
                "\t\t\t\t\"discount\": 2.7,\n" +
                "\t\t\t\t\"oilName\": \"汽油98号\",\n" +
                "\t\t\t\t\"user\": \"T9999998754\",\n" +
                "\t\t\t\t\"cash\": 267.3\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:35:28\",\n" +
                "\t\t\t\t\"money\": 218,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"4217191728799\",\n" +
                "\t\t\t\t\"discount\": 0,\n" +
                "\t\t\t\t\"oilName\": \"汽油95号\",\n" +
                "\t\t\t\t\"user\": \"T9999994217\",\n" +
                "\t\t\t\t\"cash\": 218\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:33:03\",\n" +
                "\t\t\t\t\"money\": 276,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"7357191583503\",\n" +
                "\t\t\t\t\"discount\": 2.76,\n" +
                "\t\t\t\t\"oilName\": \"柴油-50号\",\n" +
                "\t\t\t\t\"user\": \"T9999997357\",\n" +
                "\t\t\t\t\"cash\": 273.24\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:32:37\",\n" +
                "\t\t\t\t\"money\": 252,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"1604191557449\",\n" +
                "\t\t\t\t\"discount\": 2.52,\n" +
                "\t\t\t\t\"oilName\": \"汽油92号\",\n" +
                "\t\t\t\t\"user\": \"T9999991604\",\n" +
                "\t\t\t\t\"cash\": 249.48\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:30:25\",\n" +
                "\t\t\t\t\"money\": 239,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"1840191425201\",\n" +
                "\t\t\t\t\"discount\": 9.56,\n" +
                "\t\t\t\t\"oilName\": \"天然气LNG\",\n" +
                "\t\t\t\t\"user\": \"T9999991840\",\n" +
                "\t\t\t\t\"cash\": 229.44\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:29:37\",\n" +
                "\t\t\t\t\"money\": 294,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"5456191377101\",\n" +
                "\t\t\t\t\"discount\": 14.7,\n" +
                "\t\t\t\t\"oilName\": \"天然气CNG\",\n" +
                "\t\t\t\t\"user\": \"T9999995456\",\n" +
                "\t\t\t\t\"cash\": 279.3\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:28:54\",\n" +
                "\t\t\t\t\"money\": 225,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"2101191334990\",\n" +
                "\t\t\t\t\"discount\": 0,\n" +
                "\t\t\t\t\"oilName\": \"柴油10号\",\n" +
                "\t\t\t\t\"user\": \"T9999992101\",\n" +
                "\t\t\t\t\"cash\": 225\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:25:27\",\n" +
                "\t\t\t\t\"money\": 269,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"7140191127516\",\n" +
                "\t\t\t\t\"discount\": 10.76,\n" +
                "\t\t\t\t\"oilName\": \"汽油92号\",\n" +
                "\t\t\t\t\"user\": \"T9999997140\",\n" +
                "\t\t\t\t\"cash\": 258.24\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:24:39\",\n" +
                "\t\t\t\t\"money\": 203,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"1843191079421\",\n" +
                "\t\t\t\t\"discount\": 0,\n" +
                "\t\t\t\t\"oilName\": \"柴油-35号\",\n" +
                "\t\t\t\t\"user\": \"T9999991843\",\n" +
                "\t\t\t\t\"cash\": 203\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:24:33\",\n" +
                "\t\t\t\t\"money\": 263,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"2528191073397\",\n" +
                "\t\t\t\t\"discount\": 13.15,\n" +
                "\t\t\t\t\"oilName\": \"柴油-20号\",\n" +
                "\t\t\t\t\"user\": \"T9999992528\",\n" +
                "\t\t\t\t\"cash\": 249.85\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:22:17\",\n" +
                "\t\t\t\t\"money\": 290,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"9696190937080\",\n" +
                "\t\t\t\t\"discount\": 0,\n" +
                "\t\t\t\t\"oilName\": \"柴油-10号\",\n" +
                "\t\t\t\t\"user\": \"T9999999696\",\n" +
                "\t\t\t\t\"cash\": 290\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:21:35\",\n" +
                "\t\t\t\t\"money\": 261,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"1198190895942\",\n" +
                "\t\t\t\t\"discount\": 0,\n" +
                "\t\t\t\t\"oilName\": \"汽油90号\",\n" +
                "\t\t\t\t\"user\": \"T9999991198\",\n" +
                "\t\t\t\t\"cash\": 261\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"oilOrderTime\": \"2022-01-03T14:20:52\",\n" +
                "\t\t\t\t\"money\": 292,\n" +
                "\t\t\t\t\"coupon\": 0,\n" +
                "\t\t\t\t\"balance\": 0,\n" +
                "\t\t\t\t\"oilOrderId\": \"1088190852844\",\n" +
                "\t\t\t\t\"discount\": 14.6,\n" +
                "\t\t\t\t\"oilName\": \"汽油92号\",\n" +
                "\t\t\t\t\"user\": \"T9999991088\",\n" +
                "\t\t\t\t\"cash\": 277.4\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"todayMoney\": 152971\n" +
                "\t}\n" +
                "}";
        oilOrderLists = (new Gson().fromJson(sJson,OrderLastJson.class)).getData().getOilOrderList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}