package com.cuse.myandroidpos.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;
import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.databinding.FragmentItemDetailBinding;
import com.cuse.myandroidpos.utils.SunmiPrintHelper;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    private FragmentItemDetailBinding binding;

    private TextView oilOrderId;
    private TextView oilOrderTime;
    private TextView user;
    private TextView oilName;
    private TextView money;
    private TextView discount;
    private TextView coupon;
    private TextView balance;
    private TextView cash;
    private Button print;
    private Button refund;

    private OilOrderList oilOrder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.containsKey("LastOilOrder")){
            oilOrder = new OilOrderList();
            oilOrder = (OilOrderList) bundle.getSerializable("LastOilOrder");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //找到对象，设置文字
        setText(view);

        //打印按钮功能
        print(view);

        //语音
        speech();

        //退单按钮功能
        chargeBack(view);

        // Show the placeholder content as text in a TextView & in the toolbar if available.
        updateContent();
//        rootView.setOnDragListener(dragListener);
    }

    //语音
    private void speech() {
        //        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                if (i == TextToSpeech.SUCCESS) {
//                    textToSpeech.setLanguage(Locale.CHINESE);
//                }
//            }
//        });
    }

    //退单
    private void chargeBack(View view) {
        Button btn_back = view.findViewById(R.id.btn_detail_refund);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("oilOrderId", oilOrder.getOilOrderId());
                Navigation.findNavController(getView()).navigate(R.id.detail_to_backProcess, bundle);
            }
        });
//        String data = "hi";
//        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
    }

    //打印
    private void print(View rootView) {
        print = rootView.findViewById(R.id.btn_detail_print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "加油ID:" + oilOrder.getOilOrderId() + "\n" +
                        "加油时间:" + oilOrder.getOilOrderTime() + "\n" +
                        "用户手机号:" + oilOrder.getUser() + "\n" +
                        "油品名称:" + oilOrder.getOilName() + "\n" +
                        "加油总额:" + oilOrder.getMoney() + "\n" +
                        "折扣金额:" + oilOrder.getDiscount() + "\n" +
                        "优惠卷金额:" + oilOrder.getCoupon() + "\n" +
                        "会员账户支付金额:" + oilOrder.getBalance() + "\n" +
                        "微信支付金额:" + oilOrder.getCash();

                float size = 24;
                String testFont = null;
                boolean isBold = true;
                boolean isUnderLine = true;
                SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine, testFont);
                SunmiPrintHelper.getInstance().feedPaper();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    private void updateContent() {
//        if (mItem != null) {
//            mTextView.setText("id: " + mItem.getOilOrderId() + " time: " + mItem.getOilOrderTime()
//                    + " oil: " + mItem.getOil() + " money: " + mItem.getMoney());
//            if (mToolbarLayout != null) {
//                mToolbarLayout.setTitle("订单id: " + mItem.getOilOrderId());
//            }
//        }
    }
    private void setText(View rootView) {
        oilOrderId = rootView.findViewById(R.id.tv_detail_oilOrderId);
        oilOrderId.setText("加油ID：" + oilOrder.getOilOrderId());

        oilOrderTime = rootView.findViewById(R.id.tv_detail_oilOrderTime);
        oilOrderTime.setText("加油时间：" + oilOrder.getOilOrderTime());

        user = rootView.findViewById(R.id.tv_detail_user);
        user.setText("用户手机号：" + oilOrder.getUser());

        oilName = rootView.findViewById(R.id.tv_detail_oilName);
        oilName.setText("油品名称：" + oilOrder.getOilName());

        money = rootView.findViewById(R.id.tv_detail_money);
        money.setText("加油总额：" + oilOrder.getMoney());

        discount = rootView.findViewById(R.id.tv_detail_discount);
        discount.setText("折扣金额：" + oilOrder.getDiscount());

        coupon = rootView.findViewById(R.id.tv_detail_coupon);
        coupon.setText("优惠券金额：" + oilOrder.getCoupon());

        balance = rootView.findViewById(R.id.tv_detail_balance);
        balance.setText("会员账户支付金额：" + oilOrder.getBalance());

        cash = rootView.findViewById(R.id.tv_detail_cash);
        cash.setText("微信支付金额：" + oilOrder.getCash());
    }
}