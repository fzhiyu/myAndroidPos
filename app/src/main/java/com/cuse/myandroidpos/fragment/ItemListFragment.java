package com.cuse.myandroidpos.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
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
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.cuse.myandroidpos.ListDataSave;
import com.cuse.myandroidpos.PosWebSocket.WebSocketClientService;
import com.cuse.myandroidpos.PosWebSocket.wsInfo;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;
import com.cuse.myandroidpos.activity.LoginActivity;
import com.cuse.myandroidpos.activity.MainActivity;
import com.cuse.myandroidpos.adapter.HomeAdapter;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;
import com.cuse.myandroidpos.utils.SunmiPrintHelper;
import com.google.gson.Gson;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
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

    private LinkedList<OilOrderList> oilOrderLists;
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
    private long timeStamp;
    private String stringBuffer;
    private String signature;
    private Handler handler;
    private Runnable runnable;
    private OrderLastJson orderLastJson;
    private int newOrderNum;
    private TextToSpeech textToSpeech;
    private View sView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);

        mContext = getContext();

        //得到login传来的intent
        token = ((MainActivity) getActivity()).getToken();

        //初始化语音engine
        initSpeech();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sView = view;
        oilOrderLists = new LinkedList<>();
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

        orderLastPost();
        //按钮功能
        setButton(view);
        //下拉刷新
        handleDownPullUpdate();

        //连接webSocket
//        ws_connect();

        example();
    }

    private com.cuse.myandroidpos.PosWebSocket.WebSocketClient client;
//    private WebSocketClientService.WebSocketClientBinder binder;

    private void example() {
        initData();
        URI uri;
        try {
            uri = new URI("ws://paas.u-coupon.cn/wss");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        com.cuse.myandroidpos.PosWebSocket.WebSocketClient client =
                new com.cuse.myandroidpos.PosWebSocket.WebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                Log.e(TAG, "连接: " + message);
                if(message.contains("{")) {
                    orderLastPost();
                }
            }
        };
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (client != null && client.isOpen()) {
            client.send(json_login);
            client.send(json_checkOnline);
        }
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
//                client.send(json_login);
                client.send(json_heart);
                handler.postDelayed(this, 20000);
            }
        };
        handler.postDelayed(runnable, 20000);
    }



    private void ws_connect () {
        initData();
        //建立websockets连接
        createWebSocketClient();
        //login
        webSocketClient.send(json_login);
        //定时发送heartbeat
        heartBeat();
    }

    //每隔20s发送心跳
    private void heartBeat() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                webSocketClient.send(json_heart);
                handler.postDelayed(this, 20000);
            }
        };
        handler.postDelayed(runnable, 20000);
    }

    //关闭定时器
    @Override
    public void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(runnable);
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
        stationId = "BJ001001";
        wsInfo_login = new wsInfo(stationId, "login");
        json_login = JSON.toJSONString(wsInfo_login);

        wsInfo_checkOnline = new wsInfo("", "check_online");
        json_checkOnline = JSON.toJSONString(wsInfo_checkOnline);

        wsInfo_heart = new wsInfo(stationId, "heartbeat");
        json_heart = JSON.toJSONString(wsInfo_heart);

        wsInfo_newOrder = new wsInfo(stationId, "new_order");
        json_newOrder = JSON.toJSONString(wsInfo_newOrder);
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
                Log.e(TAG, "onTextReceived: " + message);
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
        webSocketClient.setReadTimeout(6000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();

    }

    public void orderLastPost() {
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

        timeStamp = new Date().getTime();
//        Log.i("")
        //得到字符串并加密编码
        stringBuffer = "timestamp" +
                timeStamp / 1000 +
                "token" +
                token +
                LoginActivity.interferenceCode;
        signature = Tools.md5.md5(stringBuffer);

        //使用Retrofit进行post
        Call<OrderLastJson> call = httpBinService.orderLast(token, timeStamp / 1000 + "", signature);
        call.enqueue(new Callback<OrderLastJson>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<OrderLastJson> call, Response<OrderLastJson> response) {
                swipeRefreshLayout.setRefreshing(false);
                orderLastJson = response.body();
                //
                Gson gson = new Gson();
                String s = gson.toJson(orderLastJson);
                assert orderLastJson != null;
//                Log.i("应答编码", "" + orderLastJson.getCode());
//                Log.i("stringBuffer", "" + stringBuffer);
//                Log.i("签名", "" + signature);

                if (response.body().getCode() == 0) {
                    //设置显示总金钱和总订单
                    tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() + "");
                    tvTotalOrder.setText(orderLastJson.getData().getTodayCount() + "");
                    //加入新订单
                    addOrder();

                    //进行语音播报
                    if (getVoiceValue())
                        newOrderSpeech();

                    //新订单打印
                    if (getPrintValue())
                        newOrderPrint();
                    //列表
                    recyclerView = binding.itemList;
                    //recycleView,适配器单独写在了HomeAdapter
                    setRecyclerView(recyclerView);

// 列表刷新                   homeAdapter.notifyDataSetChanged();
                } else
                    Tools.codeError(getContext(), orderLastJson.getCode());
            }

            @Override
            public void onFailure(Call<OrderLastJson> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //新订单打印
    private void newOrderPrint() {
        if (oilOrderLists != null && newOrderNum !=0) {
            String content = "加油ID:" + oilOrderLists.get(0).getOilOrderId() + "\n" +
                    "加油时间:" + oilOrderLists.get(0).getOilOrderTime() + "\n" +
                    "用户手机号:" + oilOrderLists.get(0).getUser() + "\n" +
                    "油品名称:" + oilOrderLists.get(0).getOilName() + "\n" +
                    "加油总额:" + oilOrderLists.get(0).getMoney() + "\n" +
                    "折扣金额:" + oilOrderLists.get(0).getDiscount() + "\n" +
                    "优惠卷金额:" + oilOrderLists.get(0).getCoupon() + "\n" +
                    "会员账户支付金额:" + oilOrderLists.get(0).getBalance() + "\n" +
                    "微信支付金额:" + oilOrderLists.get(0).getCash();

            float size = 24;
            String testFont = null;
            boolean isBold = true;
            boolean isUnderLine = true;
            SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine, testFont);
            SunmiPrintHelper.getInstance().feedPaper();
        }
    }

    //新订单语音播报
    private void newOrderSpeech() {
        if (oilOrderLists != null && newOrderNum != 0) {
            String phone = oilOrderLists.get(0).getUser();
            String subPhone = phone.substring(phone.length() - 4);
            String aPhone = subPhone.charAt(0) + "-"
                    + subPhone.charAt(1) + "-"
                    + subPhone.charAt(2) + "-"
                    + subPhone.charAt(3);
            String oilName = oilOrderLists.get(0).getOilName();
            String[] aOilName = oilName.split("-");
            StringBuilder speechOilName = new StringBuilder();
            for (String s : aOilName) {
                speechOilName.append(s);
            }
            String oilMoney = String.valueOf(oilOrderLists.get(0).getMoney());
            Log.e(TAG, "newOrderSpeech: " + phone);
            String data = "新订单，手机尾号" + aPhone + "," + speechOilName + oilMoney + "元";
            textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    //判断新旧订单，如果有新订单就加到就订单上面，并移除最后订单
    private void addOrder() {
        List<OilOrderList> tmp = orderLastJson.getData().getOilOrderList();

        newOrderNum = 0;
        if (oilOrderLists.size() == 0) {
            //判读是否有新订单
            oilOrderLists.addAll(orderLastJson.getData().getOilOrderList());
        } else {
            newOrderNum = judgeNewOrder(orderLastJson.getData().getOilOrderList());
        }
        Log.e("oilOrderLists.size", "onResponse: " + tmp.get(0).getOilOrderTime());
//                    Log.e(TAG, "onResponse: " + newOrderNum);

        for (int i = newOrderNum - 1; i >= 0; i--) {
            oilOrderLists.removeLast();
            oilOrderLists.addFirst(orderLastJson.getData().getOilOrderList().get(i));

        }

        if (newOrderNum == 0) {
            Toast.makeText(getContext(), "无新订单", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "有" + newOrderNum + "笔新订单"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    //判断新订单
    private int judgeNewOrder(List<OilOrderList> newOrderList) {
        for (int i = 0; i < newOrderList.size(); i++) {
//            if(t.getOilOrderTime())
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date oldDate = sdf.parse(oilOrderLists.get(0).getOilOrderTime());
                Date newDate = sdf.parse(newOrderList.get(i).getOilOrderTime());
                if (newDate.equals(oldDate)) {
                    return i;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 20;
    }

    public void setButton(View view) {
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
        homeAdapter = new HomeAdapter(oilOrderLists, newOrderNum, getActivity());
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
                bundle.putSerializable("LastOilOrder", oilOrderLists.get(position));

                Navigation.findNavController(getView()).navigate(R.id.show_item_detail, bundle);
            }
        });
    }

    //下拉刷新
    private void handleDownPullUpdate() {
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
    public void setInternetLayout() {
        if (internet == 1) {
            TextView internet = getView().findViewById(R.id.tv_home_internet);
            internet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initSpeech() {
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });
    }

    private boolean getPrintValue() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean print = prefs.getBoolean("print",false);
        return print;
    }

    private Boolean getVoiceValue(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean voice = prefs.getBoolean("voice",false);
        return voice;
    }
}