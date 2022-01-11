package com.cuse.myandroidpos.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.cuse.myandroidpos.PosWebSocket.wsInfo;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.adapter.HomeAdapter;
import com.cuse.myandroidpos.ListDataSave;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;

import com.cuse.myandroidpos.md5;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import tech.gusavila92.websocketclient.WebSocketClient;

public class ItemListFragment extends Fragment {

    private FragmentItemListBinding binding;
    private long currentTimeStamp;//订单刷新时间
    //private OrderLastJson orderLastJson;//储存得到的订单数据

    private List<OilOrderList> oilOrderLists;
    private HomeAdapter homeAdapter;

    private int internet = 0;//网络连接参量，0代表连上，1代表断开

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
    private String token;

    private Context mContext;
    private ListDataSave dataSave;

    private WebSocketClient webSocketClient;

    private wsInfo wsInfo_login;
    private wsInfo wsInfo_logout;
    private wsInfo wsInfo_heart;
    private wsInfo wsInfo_newOrder;
    private wsInfo wsInfo_pushNew;
    private wsInfo wsInfo_checkOnline;
    private wsInfo wsInfo_isOnlineNo;
    private String json_login;
    private String json_logout;
    private String json_heart;
    private String json_newOrder;
    private String json_pushNew;
    private String json_checkOnline;
    private String json_isOnlineNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);

        mContext = getContext();

        //得到login传来的intent
        token = ((MainActivity)getActivity()).getToken();

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
        //找到按钮实例,搜索，汇总，退单，设置
        btnSearch = view.findViewById(R.id.btn_home_search);
        btnAll = view.findViewById(R.id.btn_home_all);
        btnRefund = view.findViewById(R.id.btn_home_refund);
        btnSet = view.findViewById(R.id.btn_home_set);

        //网络不好时弹出未连接网络的框
        setInternetLayout();

        //按钮功能
        setButton(view);
        //下拉刷新
        handleDownPullUpdate();

        initData();
        testWebsockets(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.navigation_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            orderLastPost();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    //初始发送数据
    private void initData() {
        wsInfo_login = new wsInfo(token,"login");
        json_login = JSON.toJSONString(wsInfo_login);
        wsInfo_checkOnline = new wsInfo("", "check_online");
        json_checkOnline = JSON.toJSONString(wsInfo_checkOnline);
    }

    //测试websockets
    private void testWebsockets(View view) {
        Button btn_test_ws = view.findViewById(R.id.btn_Test);
        btn_test_ws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWebSocketClient();
                webSocketClient.send(json_login);
                webSocketClient.send(json_checkOnline);
            }
        });
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://paas.u-coupon.cn/wss");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
            }

            @Override
            public void onTextReceived(String message) {
                Log.i("received", "" + message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
//                Log.i("Ping", "" + Arrays.toString(data));
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
//                System.out.println(e.getMessage());
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
//                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    public void orderLastPost(){
        oilOrderLists = new ArrayList<>();
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

        long timeStamp = new Date().getTime();
//        Log.i("")
        //得到字符串并加密编码
        String stringBuffer = "timestamp" +
                timeStamp / 1000 +
                "token" +
                token +
                LoginActivity.interferenceCode;
        String signature = md5.md5(stringBuffer);

        //使用Retrofit进行post
        Call<OrderLastJson> call = httpBinService.orderLast(token,timeStamp / 1000 + "", signature);
        call.enqueue(new Callback<OrderLastJson>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<OrderLastJson> call, Response<OrderLastJson> response) {
                swipeRefreshLayout.setRefreshing(false);
                OrderLastJson orderLastJson = response.body();
                //
                Gson gson = new Gson();
                String s = gson.toJson(orderLastJson);
                assert orderLastJson != null;
//                Log.i("应答编码", "" + orderLastJson.getCode());
//                Log.i("stringBuffer", "" + stringBuffer);
//                Log.i("签名", "" + signature);

                if (response.body().getCode() == 0) {
                    //设置显示总金钱和总订单
                    Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                    tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() + "");
                    tvTotalOrder.setText(orderLastJson.getData().getTodayCount() + "");
//                    for (int i = 0; i < response.body().getData().getOilOrderList().size(); i++) {
////                        Log.i("hejun", "onResponse: " + orderLastJson.getData().getOilOrderList().get(i).compareTo(oilOrderLists.get(0)));
//                        if (oilOrderLists.get(0).compareTo(orderLastJson.getData().getOilOrderList().get(i)) >= 0) {
//                            oilOrderLists.add(0, response.body().getData().getOilOrderList().get(i));
//                        }
//                    }

                    oilOrderLists.addAll(orderLastJson.getData().getOilOrderList());
                    //列表
                    recyclerView = binding.itemList;
                    //recycleView,适配器单独写在了HomeAdapter
                    setRecyclerView(recyclerView);

//                    homeAdapter.notifyDataSetChanged();
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

    public void setButton (View view) {
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


    //网络不好时弹出未连接网络的框
    public void setInternetLayout(){
        if (internet == 1){
            TextView internet = getView().findViewById(R.id.tv_home_internet);
            internet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}