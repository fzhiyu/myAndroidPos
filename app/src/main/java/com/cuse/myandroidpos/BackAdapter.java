package com.cuse.myandroidpos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewTreeViewModelKt;
import androidx.recyclerview.widget.RecyclerView;

import com.cuse.myandroidpos.Post.RefundAllJson.OilOrder;

import java.util.List;
import java.util.logging.LoggingMXBean;

public class BackAdapter extends RecyclerView.Adapter<BackAdapter.MyViewHolder> {
    private List<OilOrder> refundOilOrderList;
    private Context context;

    public BackAdapter(List<OilOrder> refundOilOrderList, Context context){
        this.context = context;
        this.refundOilOrderList = refundOilOrderList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context,R.layout.fragment_back_list,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.refundTime.setText(refundOilOrderList.get(position).getOilOrderTime());
        holder.oilOrderTime.setText(refundOilOrderList.get(position).getOilOrderTime());
        holder.state.setText(refundOilOrderList.get(position).getRefundStatus());
        holder.phoneNumber.setText(refundOilOrderList.get(position).getUser());

    }

    @Override
    public int getItemCount() {
        return refundOilOrderList == null ? 0 : refundOilOrderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView refundTime;
        private TextView oilOrderTime;
        private TextView state;
        private TextView phoneNumber;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            refundTime = itemView.findViewById(R.id.refundTime);
            oilOrderTime = itemView.findViewById(R.id.oilOrderTime);
            state = itemView.findViewById(R.id.state);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mBackRecyclerItemClickListener != null){
                        mBackRecyclerItemClickListener.OnBackRecyclerItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    private OnBackRecyclerItemClickListener mBackRecyclerItemClickListener;

    public void setBackRecyclerItemClickListener(OnBackRecyclerItemClickListener listener){
        mBackRecyclerItemClickListener = listener;
    }

    public interface OnBackRecyclerItemClickListener{
        void OnBackRecyclerItemClick(int position);
    }
}
