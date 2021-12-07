package com.cuse.myandroidpos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private List<OilOrderList> homeOilOrderList;
    private Context context;
    private OnHomeRecyclerItemClickListener mHomeRecyclerItemClickListener;

    public HomeAdapter(List<OilOrderList> homeOilOrderList, Context context) {
        this.homeOilOrderList = homeOilOrderList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context,R.layout.fragment_home_item_content,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.oilOrderId.setText(homeOilOrderList.get(position).getOilOrderId());
        holder.oil.setText(homeOilOrderList.get(position).getOilName());
        holder.money.setText(homeOilOrderList.get(position).getMoney() + "");
        holder.oilOrderTime.setText(homeOilOrderList.get(position).getOilOrderTime());
        holder.user.setText(homeOilOrderList.get(position).getUser());
    }

    @Override
    public int getItemCount() {
        return homeOilOrderList == null ? 0 : homeOilOrderList.size(); }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView oilOrderId;
        private TextView oil;
        private TextView money;
        private TextView oilOrderTime;
        private TextView user;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oilOrderId = itemView.findViewById(R.id.tv_homeItem_oilOrderId);
            oil = itemView.findViewById(R.id.tv_homeItem_oil);
            money = itemView.findViewById(R.id.tv_homeItem_money);
            oilOrderTime = itemView.findViewById(R.id.tv_homeItem_oilOrderTime);
            user = itemView.findViewById(R.id.tv_homeItem_user);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mHomeRecyclerItemClickListener != null){
                        mHomeRecyclerItemClickListener.OnHomeRecyclerItemClick(getPosition());
                    }
                }
            });
        }

    }

    public void setHomeRecyclerItemClickListener(OnHomeRecyclerItemClickListener mHomeRecyclerItemClickListener) {
        this.mHomeRecyclerItemClickListener = mHomeRecyclerItemClickListener;
    }

    public interface OnHomeRecyclerItemClickListener{
        void OnHomeRecyclerItemClick(int position);
    }
}
