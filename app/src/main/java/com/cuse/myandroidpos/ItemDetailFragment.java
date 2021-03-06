package com.cuse.myandroidpos;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.DragEvent;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import com.cuse.myandroidpos.databinding.FragmentItemDetailBinding;

import org.w3c.dom.ls.LSOutput;

import java.util.Locale;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The placeholder content this fragment is presenting.
     */
    private MyListData mItem;
    private Toolbar mToolbarLayout;
    private TextView mTextView;
    private TextToSpeech textToSpeech;
    private FragmentItemDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MyData.CreatData().get(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)) - 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mToolbarLayout = rootView.findViewById(R.id.detail_toolbar);
        mTextView = binding.itemDetail;
//        Button print_btn = rootView.findViewById(R.id.fab);
//        print_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String textToPrint = String.format(Locale.ROOT, "test print", 1);
//
//                String url = "rawbt:" + textToPrint;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
//            }
//        });
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });

        Button speech_btn = (Button) rootView.findViewById(R.id.SpeechButton);
        speech_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "hi";
                textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        // Show the placeholder content as text in a TextView & in the toolbar if available.
        updateContent();
//        rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    private void updateContent() {
        if (mItem != null) {
            mTextView.setText("id: " + mItem.getOilOrderId() + " time: " + mItem.getOilOrderTime()
                    + " oil: " + mItem.getOil() + " money: " + mItem.getMoney());
            if (mToolbarLayout != null) {
                mToolbarLayout.setTitle("??????id: " + mItem.getOilOrderId());
            }
        }
    }
}