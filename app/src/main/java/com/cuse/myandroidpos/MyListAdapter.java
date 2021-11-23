package com.cuse.myandroidpos;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private static ArrayList<MyListData> listdata;
//    private ArrayList<MyListData> listdata;

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<MyListData> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    //In the onBindViewHolder() method each data items are set to each row.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata.get(position);
        holder.oilOrderId.setText("id: " + myListData.getOilOrderId() + "; ");
        holder.oilOrderTime.setText("time: " + myListData.getOilOrderTime() + "; ");
        holder.user.setText("phone: " + myListData.getUser() + "; ");
        holder.oil.setText("oil: " + myListData.getOil() + "; ");
        holder.money.setText("money: " + myListData.getMoney() + "; ");
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+
                        myListData.getOilOrderId(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView oilOrderId;
        public TextView oilOrderTime;
        public TextView user;
        public TextView oil;
        public TextView money;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.oilOrderId = (TextView) itemView.findViewById(R.id.oilOrderId);
            this.oilOrderTime = (TextView) itemView.findViewById(R.id.oilOrderTime);
            this.user = (TextView) itemView.findViewById(R.id.user);
            this.oil = (TextView) itemView.findViewById(R.id.oil);
            this.money = (TextView) itemView.findViewById(R.id.money);

            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
