package com.cuse.myandroidpos;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.cuse.myandroidpos.databinding.ItemListContentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * A fragment representing a list of Items. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListFragment extends Fragment {
    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */

//    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
//            Toast.makeText(
//                    v.getContext(),
//                    "Undo (Ctrl + Z) shortcut triggered",
//                    Toast.LENGTH_LONG
//            ).show();
//            return true;
//        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
//            Toast.makeText(
//                    v.getContext(),
//                    "Find (Ctrl + F) shortcut triggered",
//                    Toast.LENGTH_LONG
//            ).show();
//            return true;
//        }
//        return false;
//    };

    private FragmentItemListBinding binding;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.navigation_menu, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_Search: {
//                System.out.println("search");
//                return true;
//            }
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        //binding -> app:id/item_list_container -> fragment_item_list
//        System.out.println("binding(ItemListFragment): " + binding.getRoot());
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

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.blue)));

//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(toolbar);
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.inflateMenu(R.menu.navigation_menu);
//        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            MyListData item =
                    (MyListData) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getOilOrderId());
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, arguments);
            } else {
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
            }
        };

        /*
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        View.OnContextClickListener onContextClickListener = itemView -> {
            MyListData item =
                    (MyListData) itemView.getTag();
            Toast.makeText(
                    itemView.getContext(),
                    "Context click of item " + item.getOilOrderId(),
                    Toast.LENGTH_LONG
            ).show();
            return true;
        };

        setupRecyclerView(recyclerView, onClickListener, onContextClickListener);
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener,
            View.OnContextClickListener onContextClickListener
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                MyData.CreatData(),
                onClickListener,
                onContextClickListener
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
        private final View.OnContextClickListener mOnContextClickListener;

        SimpleItemRecyclerViewAdapter(List<MyListData> items,
                                      View.OnClickListener onClickListener,
                                      View.OnContextClickListener onContextClickListener) {
            mValues = items;
            mOnClickListener = onClickListener;
            mOnContextClickListener = onContextClickListener;
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setOnContextClickListener(mOnContextClickListener);
            }
            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).getOilOrderId());
                ClipData dragData = new ClipData(
                        ((MyListData) v.getTag()).getMoney(),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
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
        //ViewHolder end
    }
    //SimpleItemRecyclerViewAdapter end
}
//ItemListFragment end