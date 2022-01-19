package com.cuse.myandroidpos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.Tools;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private List<OilOrderList> homeOilOrderList;
    private Context context;
    private int newOrderNum;
    private OnHomeRecyclerItemClickListener mHomeRecyclerItemClickListener;

    public HomeAdapter(List<OilOrderList> homeOilOrderList, int newOrderNum, Context context) {
        this.homeOilOrderList = homeOilOrderList;
        this.newOrderNum = newOrderNum;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_home_item_content,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.oilOrderId.setText(homeOilOrderList.get(position).getOilOrderId());
        holder.oil.setText(homeOilOrderList.get(position).getOilName());
        holder.money.setText("¥ " + homeOilOrderList.get(position).getMoney());
//        holder.oilOrderTime.setText(Tools.NoT(homeOilOrderList.get(position).getOilOrderTime()));
        holder.oilOrderTime.setText(homeOilOrderList.get(position).getOilOrderTime());
//        //隐藏手机号的中间四位
//        StringBuffer sb = new StringBuffer(homeOilOrderList.get(position).getUser());
//        sb.replace(3,7,"****");
        holder.user.setText(homeOilOrderList.get(position).getUser());
        //取消之前的标记
        for (int i = 0; i < homeOilOrderList.size(); i++)
            holder.newOrderSign.setVisibility(View.GONE);
        //订单的位置小于newOrderNum，显示新订单标识
        if (position < newOrderNum){
            holder.newOrderSign.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (homeOilOrderList == null)
            return 0;
        else if (homeOilOrderList.size() <= 20)
            return homeOilOrderList.size();
        else
            return 20;
         }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView oilOrderId;
        private TextView oil;
        private TextView money;
        private TextView oilOrderTime;
        private TextView user;
        private TextView newOrderSign;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oilOrderId = itemView.findViewById(R.id.tv_homeItem_oilOrderId);
            oil = itemView.findViewById(R.id.tv_homeItem_oil);
            money = itemView.findViewById(R.id.tv_homeItem_money);
            oilOrderTime = itemView.findViewById(R.id.tv_homeItem_oilOrderTime);
            user = itemView.findViewById(R.id.tv_homeItem_user);
            newOrderSign = itemView.findViewById(R.id.newOrderSign);

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
