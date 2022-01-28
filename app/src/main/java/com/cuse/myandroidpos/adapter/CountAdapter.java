package com.cuse.myandroidpos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.OrderSummaryJson.OilOrderList;
import com.cuse.myandroidpos.R;

import java.util.List;

public class CountAdapter extends RecyclerView.Adapter<CountAdapter.MyViewHolder> {

    private List<OilOrderList> homeOilOrderList;
    private Context context;

    public CountAdapter(List<OilOrderList> homeOilOrderList, Context context) {
        this.homeOilOrderList = homeOilOrderList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.count_item_content,null);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.oilId.setText(homeOilOrderList.get(position).getOilId());
        holder.oilName.setText(homeOilOrderList.get(position).getOilName());
        holder.oilCount.setText("订单数：" + homeOilOrderList.get(position).getStringOilCount());
        holder.oilMoney.setText("金额： " + homeOilOrderList.get(position).getStringOilMoney());
    }

    @Override
    public int getItemCount() {
        return homeOilOrderList == null ? 0 : homeOilOrderList.size(); }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

//        private final TextView oilId;
        private final TextView oilName;
        private TextView oilCount;
        private TextView oilMoney;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            oilId = itemView.findViewById(R.id.count_oil_Id);
            oilName = itemView.findViewById(R.id.count_oil_Name);
            oilCount = itemView.findViewById(R.id.oil_Count);
            oilMoney = itemView.findViewById(R.id.count_item_Money);
        }
    }
}
