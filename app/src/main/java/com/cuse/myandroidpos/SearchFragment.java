package com.cuse.myandroidpos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuse.myandroidpos.databinding.FragmentSearchBinding;
import com.cuse.myandroidpos.databinding.ItemListContentBinding;

import java.util.List;


public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
//        System.out.println("binding.getRoot() Search: " + binding.getRoot())
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.itemList2;

        View.OnClickListener onClickListener = itemView -> {
            MyListData item = (MyListData) itemView.getTag();
            Bundle argument = new Bundle();
            argument.putString(ItemDetailFragment.ARG_ITEM_ID, item.getOilOrderId());
            Navigation.findNavController(itemView).navigate(R.id.show_search, argument);
        };



        setupRecyclerView(recyclerView, onClickListener);
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


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getOilOrderId());
            holder.mContentView.setText(mValues.get(position).getOilOrderTime());
            holder.moneyView.setText(String.valueOf(mValues.get(position).getMoney()));
            holder.oilView.setText(mValues.get(position).getOil());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mIdView;
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