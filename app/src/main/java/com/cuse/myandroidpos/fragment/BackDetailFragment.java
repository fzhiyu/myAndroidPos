package com.cuse.myandroidpos.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuse.myandroidpos.Post.RefundAllJson.OilOrder;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.databinding.FragmentBackDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BackDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private OilOrder oilOrder;//传入的订单对象
    private FragmentBackDetailBinding binding;

    private TextView refundId;
    private TextView refundRequestTime;
    private TextView refundStatus;
    private TextView refundReason;
    private TextView refundTime;
    private TextView oilOrderId;
    private TextView oilOrderTime;
    private TextView user;
    private TextView oilName;
    private TextView money;
    private TextView discount;
    private TextView coupon;
    private TextView balance;
    private TextView cash;


    public BackDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BackDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackDetailFragment newInstance(String param1, String param2) {
        BackDetailFragment fragment = new BackDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.containsKey("RefundOilOrder")) {
            oilOrder = (OilOrder) bundle.getSerializable("RefundOilOrder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_back_detail, container, false);
        binding = FragmentBackDetailBinding.inflate(inflater,container,false);
        View rootView = binding.getRoot();

        //关联TextView的ID
        refundId = rootView.findViewById(R.id.tv_refund_detail_refundId);
        refundId.setText("退单ID：" + oilOrder.getRefundId());

        refundRequestTime = rootView.findViewById(R.id.tv_refund_detail_refundRequestTime);
        refundRequestTime.setText("退单请求时间：" + oilOrder.getRefundRequestTime());

        refundStatus = rootView.findViewById(R.id.tv_refund_detail_refundStatus);
        refundStatus.setText("退单状态：" + oilOrder.getRefundStatus());

        refundReason = rootView.findViewById(R.id.tv_refund_detail_refundReason);
        refundReason.setText("退单原因：" + oilOrder.getRefundReason());

        refundTime = rootView.findViewById(R.id.tv_refund_detail_refundTime);
        refundTime.setText("退单处理时间" + oilOrder.getRefundTime());

        oilOrderId = rootView.findViewById(R.id.tv_refund_detail_oilOrderId);
        oilOrderId.setText("加油ID：" + oilOrder.getOilOrderId());

        oilOrderTime = rootView.findViewById(R.id.tv_refund_detail_oilOrderTime);
        oilOrderTime.setText("加油时间：" + oilOrder.getOilOrderTime());

        user = rootView.findViewById(R.id.tv_refund_detail_user);
        user.setText("用户手机号：" + oilOrder.getUser());

        oilName = rootView.findViewById(R.id.tv_refund_detail_oilName);
        oilName.setText("油品名称：" + oilOrder.getOilName());

        money = rootView.findViewById(R.id.tv_refund_detail_money);
        money.setText("加油总额：" + oilOrder.getMoney());

        discount = rootView.findViewById(R.id.tv_refund_detail_discount);
        discount.setText("折扣金额：" + oilOrder.getDiscount());

        coupon = rootView.findViewById(R.id.tv_refund_detail_coupon);
        coupon.setText("优惠券金额：" + oilOrder.getCoupon());

        balance = rootView.findViewById(R.id.tv_refund_detail_balance);
        balance.setText("会员账户支付金额：" + oilOrder.getBalance());

        cash = rootView.findViewById(R.id.tv_refund_detail_cash);
        cash.setText("微信支付金额：" + oilOrder.getCash());




        return rootView;
    }
}