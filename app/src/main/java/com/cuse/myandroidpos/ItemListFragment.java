package com.cuse.myandroidpos;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.databinding.FragmentItemListBinding;
import com.cuse.myandroidpos.databinding.FragmentSearchBinding;
import com.cuse.myandroidpos.databinding.ItemListContentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import tech.gusavila92.websocketclient.WebSocketClient;

public class ItemListFragment extends Fragment {

    private FragmentItemListBinding binding;
    private WebSocketClient webSocketClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        //binding -> app:id/item_list_container -> fragment_item_list
//        System.out.println("binding.getRoot(): " + binding.getRoot());
        setNum();

        return binding.getRoot();
    }

    public void setNum() {
        ArrayList<MyListData> myListData = MyData.CreatData();
        float all_oil_money = 0;
        int all_trac_num = 0;
        for (MyListData m : myListData) {
            all_oil_money += m.getFloatMoney();
            all_trac_num += 1;
        }

        TextView oil_all_num = binding.getRoot().findViewById(R.id.oil_all_num);

        oil_all_num.setText(String.valueOf(all_oil_money));
        TextView oil_trac_num = binding.getRoot().findViewById(R.id.oil_trac_num);
        oil_trac_num.setText(String.valueOf(all_trac_num));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            MyListData item =
                    (MyListData) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getOilOrderId());
            Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
        };

        Button search_btn = view.findViewById(R.id.search_button);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_search, null);
            }
        });

        Button count_btn = view.findViewById(R.id.count_button);
        count_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_count, null);
            }
        });

        Button backing_btn = view.findViewById(R.id.back_button);
        backing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_back, null);
            }
        });

        Button setting_btn = view.findViewById(R.id.setting_button);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.show_setting, null);
            }
        });

        setupRecyclerView(recyclerView, onClickListener);

        Button test_ws = (Button) view.findViewById(R.id.test_ws);
        test_ws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWebSocketClient(view);
            }
        });
    }

    private void createWebSocketClient(View view) {
        URI uri;
        try {
            uri = new URI("ws://paas.u-coupon.cn/wss");
            System.out.println("url");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                System.out.println("start");
                JSONObject obj = new JSONObject();

                try {
                    obj.put("type", "new_order");
                    obj.put("sta", "1234");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webSocketClient.send(String.valueOf(obj));
            }

            @Override
            public void onTextReceived(String message) {
                System.out.println(message);
            }

            //            @Override
//            public void onTextReceived(String s) {
//                Log.i("WebSocket", "Message received");
//                final String message = s;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try{
//                            TextView textView = view.findViewById(R.id.animalSound);
//                            textView.setText(message);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                MyData.CreatData(),
                onClickListener
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<MyListData> mValues;
        private final View.OnClickListener mOnClickListener;

        SimpleItemRecyclerViewAdapter(List<MyListData> items,
                                      View.OnClickListener onClickListener) {
            mValues = items;
            mOnClickListener = onClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding =
                    ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText("id: " + mValues.get(position).getOilOrderId() + " ");
            holder.mContentView.setText("time: " + mValues.get(position).getOilOrderTime() + " ");
            holder.moneyView.setText("money: " + String.valueOf(mValues.get(position).getMoney())+ " ");
            holder.oilView.setText("oil: " + mValues.get(position).getOil());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView oilView;
            final TextView moneyView;

            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.oilOrderId;
                mContentView = binding.oilOrderTime;
                oilView = binding.oil;
                moneyView = binding.money;
            }

        }
    }
}