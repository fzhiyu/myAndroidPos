package com.cuse.myandroidpos.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
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
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.cuse.myandroidpos.ListDataSave;
import com.cuse.myandroidpos.PosWebSocket.WebSocketClientService;
import com.cuse.myandroidpos.PosWebSocket.wsInfo;
import com.cuse.myandroidpos.Post.HttpBinService;
import com.cuse.myandroidpos.Post.OrderLastJson.Data;
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

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
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
    private Date date = new Date();//订单刷新时间
    private OrderLastJson orderLastJson;//储存得到的订单数据
    private String orderLastJsonToString;//储存得到的的订单数据的字符串

    private LinkedList<OilOrderList> oilOrderLists;
    private HomeAdapter homeAdapter;

    private boolean internet = true;//网络连接参量

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

    private int newOrderNum = 0;
    private TextToSpeech textToSpeech;
    private View sView;
    private com.cuse.myandroidpos.PosWebSocket.WebSocketClient client;

    private TextView tvRefreshView;
    private int flag = 0;
    private int leave_newOrderNum = 0;
    private TextView btn_wsStatus;

    private MediaPlayer mediaPlayer;//音频播放器
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    Thread wsThread;
    private int wsConnectFlag = 0;
    private boolean isConnected = false;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView: " );

        binding = FragmentItemListBinding.inflate(inflater, container, false);

        mContext = getContext();

        //得到login传来的intent
        if (getActivity() != null) {
            token = ((MainActivity) getActivity()).getToken();
        }

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
        //刷新时间
        tvRefreshView = view.findViewById(R.id.tv_home_refreshView);
        //下拉刷新
        swipeRefreshLayout = view.findViewById(R.id.swipe_home);
        //找到按钮实例,搜索，汇总，退单，设置
        btnSearch = view.findViewById(R.id.btn_home_search);
        btnAll = view.findViewById(R.id.btn_home_all);
        btnRefund = view.findViewById(R.id.btn_home_refund);
        btnSet = view.findViewById(R.id.btn_home_set);
        btn_wsStatus = view.findViewById(R.id.ws_status);

        //列表
        recyclerView = binding.itemList;
        //设置竖直
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //每个item的高度一定
        recyclerView.setHasFixedSize(true);
        Log.e(TAG, "onViewCreated: " );

        //读取preference
        orderLastJsonToString = sharedPref.getString("orderLatJson", "");
        //得到上次刷新的时间
        long i = sharedPref.getLong("reFreshTime", 0l);
        if (i != 0 && i != date.getTime()){
            date = new Date(i);
        }
        if (orderLastJsonToString != null && !orderLastJsonToString.equals("")) {
            Gson gson = new Gson();
            orderLastJson = gson.fromJson(orderLastJsonToString, OrderLastJson.class);
            oilOrderLists.addAll(orderLastJson.getData().getOilOrderList());
            showInformation();
        }

//        if (leave_newOrderNum == 0) {
//            orderLastPost();
//        }

        //按钮功能
        setButton(view);
        //下拉刷新
        handleDownPullUpdate();

        //连接webSocket
        ws_connect();

        //检查网络连接状态
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // network available
                Log.e(TAG, "onAvailable: ");
                internet = true;
                setInternetLayout();
            }

            @Override
            public void onLost(Network network) {
                // network unavailable
                Log.e(TAG, "onLost: ");
                internet = false;
                setInternetLayout();
            }
        };

        connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.registerDefaultNetworkCallback(networkCallback);

    }

    private void ws_connect() {
        initData();

        //初始化webSocket连接
        initWebSocketClient();

        //心跳
        heartBeat();
    }

    private void initWebSocketClient() {
        btn_wsStatus.setText("未连接");
        URI uri;
        try {
            uri = new URI("ws://paas.u-coupon.cn/wss");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                client = new com.cuse.myandroidpos.PosWebSocket.WebSocketClient(uri) {
                    @Override
                    public void onMessage(String message) {
                        super.onMessage(message);
                        //测试
                        Date date = new Date();
                        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String s = sim.format(date);
                        Log.e(TAG, "message: " + message + " " + s);

                        if (message.contains("{") && flag == 0) {
                            orderLastPost();
                        } else if (message.contains("{") && flag == 1) {
                            leave_newOrderNum++;
                        }
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        super.onClose(code, reason, remote);
                        Log.e(TAG, "onClose: " + "code:" + code + " reason:" + reason + " remote:" + remote);
                    }
                };

                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_wsStatus.setText("正在连接");
                            }
                        });
                    }

                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (client != null && client.isOpen()) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_wsStatus.setText("正常");
                            }
                        });
                    }
                    client.send(json_login);
                    client.send(json_checkOnline);
                }
            }
        }.start();
    }

    //每隔20s发送心跳
    private void heartBeat() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
//                client.close();
                if (client != null) {
                    if (client.isOpen()) {
                        btn_wsStatus.setText("正常");
                    } else {
                        btn_wsStatus.setText("未连接");
                        Log.e(TAG, "run: 重连");
//                        reconnectWs();
//                        client.send(json_login);
                        initWebSocketClient();
                    }
                } else {
                    Log.e(TAG, "run: client为空");
                    initWebSocketClient();
                }
                if (client != null && client.isOpen()) {
                    btn_wsStatus.setText("正常");
                    client.send(json_heart);
                }
                handler.postDelayed(this, 20000);
            }
        };
        handler.postDelayed(runnable, 20000);
    }

    //开启重连
    private void reconnectWs() {
        handler.removeCallbacks(runnable);

        wsThread = new Thread() {
            @Override
            public void run() {
                if (client.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                    try {
                        btn_wsStatus.setText("正在连接");
                        Log.e("JWebSocketClientService", "未连接，开启重连");
                        client.connectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (client.getReadyState().equals(ReadyState.CLOSING) ||
                        client.getReadyState().equals(ReadyState.CLOSED)) {
                    try {
                        btn_wsStatus.setText("正在连接");
                        Log.e("JWebSocketClientService", "关闭后开启重连");
                        client.connectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        wsThread.start();
    }

    //关闭定时器 退出登录
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (client != null) {
            if (client.isOpen()) {
                client.send(json_logout);
            }
            client.close();
        }

        connectivityManager.unregisterNetworkCallback(networkCallback);

        Log.e(TAG, "onDestroy: " + Thread.currentThread());
        Thread.currentThread().interrupt();

        //释放MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        flag = 1;
        leave_newOrderNum = 0;
//        handler.removeCallbacks(runnable);
//        client.close();

        //储存数据
        editor.putString("orderLatJson", orderLastJsonToString);
        editor.putLong("reFreshTime", date.getTime());
        Log.e(TAG, "onStop: " + date.getTime());
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        flag = 0;
        Log.e(TAG, "onResume: ");


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //
        Log.e(TAG, "onDestroyView: ");
        binding = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //音频初始化
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.order_notify);
        //储存初始化
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

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
        stationId = ((MainActivity) getActivity()).getStationId();
        wsInfo_login = new wsInfo(stationId, "login");
        json_login = JSON.toJSONString(wsInfo_login);

        wsInfo_logout = new wsInfo(stationId, "logout");
        json_logout = JSON.toJSONString(wsInfo_logout);

        wsInfo_checkOnline = new wsInfo("", "check_online");
        json_checkOnline = JSON.toJSONString(wsInfo_checkOnline);

        wsInfo_heart = new wsInfo(stationId, "heartbeat");
        json_heart = JSON.toJSONString(wsInfo_heart);

        wsInfo_newOrder = new wsInfo(stationId, "new_order");
        json_newOrder = JSON.toJSONString(wsInfo_newOrder);
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
                orderLastJsonToString = gson.toJson(orderLastJson);
//                Log.i("应答编码", "" + orderLastJson.getCode());
//                Log.i("stringBuffer", "" + stringBuffer);
//                Log.i("签名", "" + signature);
//                Log.i(TAG, "onResponse: " + s);

                if (response.body().getCode() == 0) {

                    //设置油站名称
                    if (orderLastJson.getData().getStationName() == null || orderLastJson.getData().getStationName().equals(""))
                        //如果为空，显示首页
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle("首页");
                    else {
                        //油站名称长度大于10，后面的显示"..."
                        StringBuffer stationName = new StringBuffer(orderLastJson.getData().getStationName());
//                      if (stationName.length() >= 10)
//                      stationName.replace(10, stationName.length() - 1 ,"...");
                        ((MainActivity) getActivity()).getSupportActionBar()
                                .setTitle(stationName);
                    }

                    //设置显示总金钱和总订单
                    tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() + "");
                    tvTotalOrder.setText(orderLastJson.getData().getTodayCount() + "");

                    //设置刷新时间
                    date = new Date();
                    SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tvRefreshView.setText(sim.format(date));

                    //加入新订单
                    addOrder();

                    //进行语音播报
                    if (getVoiceValue())
                        newOrderSpeech();

                    //新订单打印
                    if (getPrintValue())
                        newOrderPrint();


//                    //recycleView,适配器单独写在了HomeAdapter
//                    setRecyclerView(recyclerView);
//              列表刷新
                    Log.i(TAG, "onResponse: " + newOrderNum);
                    setRecyclerView(recyclerView);
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

    //信息显示
    private void showInformation() {
        //设置油站名称
        if (orderLastJson.getData().getStationName() == null || orderLastJson.getData().getStationName().equals("")){
            //如果为空，显示首页
            if (getActivity() != null) {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle("首页");
            }
        } else {
                //油站名称长度大于10，后面的显示"..."
                StringBuffer stationName = new StringBuffer(orderLastJson.getData().getStationName());
//            if (stationName.length() >= 10)
//                stationName.replace(10, stationName.length() - 1 ,"...");
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).getSupportActionBar()
                            .setTitle(stationName);
                }

            }

        //设置显示总金钱和总订单
        tvTotalMoney.setText(orderLastJson.getData().getTodayMoney() + "");
        tvTotalOrder.setText(orderLastJson.getData().getTodayCount() + "");

        //设置刷新时间
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tvRefreshView.setText(sim.format(date));

        setRecyclerView(recyclerView);
    }

    //新订单打印
    private void newOrderPrint() {
        if (oilOrderLists != null && newOrderNum != 0) {
            String content = "加油ID:" + oilOrderLists.get(0).getOilOrderId() + "\n" +
                    "加油时间:" + oilOrderLists.get(0).getOilOrderTime() + "\n" +
                    "用户手机号:" + oilOrderLists.get(0).getUser() + "\n" +
                    "油品名称:" + oilOrderLists.get(0).getOilName() + "\n" +
                    "加油总额:" + oilOrderLists.get(0).getMoney() + "\n" +
                    "折扣金额:" + oilOrderLists.get(0).getDiscount() + "\n" +
                    "优惠卷金额:" + oilOrderLists.get(0).getCoupon() + "\n" +
                    "会员账户支付金额:" + oilOrderLists.get(0).getBalance() + "\n" +
                    "微信支付金额:" + oilOrderLists.get(0).getCash();

//            Log.e(TAG, "newOrderPrint: " + content);
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
//            String aPhone = subPhone.charAt(0) + "-"
//                    + subPhone.charAt(1) + "-"
//                    + subPhone.charAt(2) + "-"
//                    + subPhone.charAt(3);
            String aPhone = Tools.numberToChineseNumber(subPhone);
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
//            mediaPlayer.start();
        }

    }

    //判断新旧订单，如果有新订单就加到就订单上面，并移除最后订单
    private void addOrder() {
        List<OilOrderList> tmp = orderLastJson.getData().getOilOrderList();

        newOrderNum = 0;
        if (oilOrderLists.size() == 0) {
            //判读是否有新订单
            oilOrderLists.addAll(orderLastJson.getData().getOilOrderList());
            if (leave_newOrderNum != 0) {
                newOrderNum = leave_newOrderNum;
                leave_newOrderNum = 0;
            }
        } else {
            newOrderNum = judgeNewOrder(orderLastJson.getData().getOilOrderList());
        }
//        Log.e("oilOrderLists.size", "onResponse: " + tmp.get(0).getOilOrderTime());
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

        //将数据填入homeAdapt
        homeAdapter = new HomeAdapter(oilOrderLists, newOrderNum, getActivity());
        recyclerView.setAdapter(homeAdapter);
//        //默认添加动画
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                if (internet)
                    orderLastPost();
                else {
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }


    //网络不好时弹出未连接网络的框
    public void setInternetLayout() {
        TextView showInternet;
        if (getView() != null) {
            showInternet = getView().findViewById(R.id.tv_home_internet);
            //internet为false控件出现
            if (!internet) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInternet.setVisibility(View.VISIBLE);
                        }
                    });
                }

            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInternet.setVisibility(View.GONE);
                        }
                    });
                }
            }

        }

    }


    private void initSpeech() {
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.setPitch(1f);
                }
            }
        });
    }

    private boolean getPrintValue() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean print = prefs.getBoolean("print", false);
        return print;
    }

    private Boolean getVoiceValue() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean voice = prefs.getBoolean("voice", false);
        return voice;
    }


}