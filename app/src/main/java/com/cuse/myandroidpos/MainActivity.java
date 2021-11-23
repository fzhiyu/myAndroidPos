package com.cuse.myandroidpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cuse.myandroidpos.databinding.ActivityItemDetailBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {



    private ArrayList<MyListData> myListData;
    private View decorView;
    private TextToSpeech textToSpeech;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityItemDetailBinding binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding.getRoot() -> app:id/container -> activity_item_detail
//        System.out.println("binding.getRoot():" + binding.getRoot());
//        setContentView(R.layout.fragment_item_list);

        System.out.println("binding.getRoot(): " + binding.getRoot());
        System.out.println("binding: " + binding);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_item_detail);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.
                Builder(navController.getGraph())
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_item_detail);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void ItemSearch(MenuItem menuItem) {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //getSupportActionBar().hide();
//        setContentView(R.layout.activity_main);
//
//        decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int i) {
//                if (i == 0) {
//                    decorView.setSystemUiVisibility(hideSystemBars());
//                }
//            }
//        });
//
//        ButterKnife.bind(this);
//
//        Speech();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//
//            }
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//
//            }
//        }
//
//        buildRecyclerView();
//    }

    public void Speech() {
        btn = (Button) findViewById(R.id.SpeechButton);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "Hi";
                Log.i("TTS", "button clicked: " + data);
                int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    protected void sendToPrint(Intent intent) {
        final String appPackageName = "ru.a402d.rawbtprinter";
        PackageManager pm = getPackageManager();

        // check app installed
        PackageInfo pi = null;
        if (pm != null) {
            try {
                pi = pm.getPackageInfo(appPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (pi == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            TextView title = new TextView(this);
            title.setText(R.string.dialog_title);
            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(14);
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.baseline_print_black_48);
            builder.setMessage(R.string.dialog_message)
                    .setView(image).setCustomTitle(title);
            builder.setPositiveButton(R.string.btn_install, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // send to print
            intent.setPackage(appPackageName);
            startActivity(intent);

        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.test1)
    public void test1(Button button) {
        MyListData curListData = myListData.get(0);
        //  "Test %d completed.\n\n\n"
        String demoStr = "id:" + curListData.getOilOrderId() + ";" +
                "time:" + curListData.getOilOrderTime() + ";" +
                "phone:" + curListData.getUser() + ";" +
                "oil:" + curListData.getOil() + ";" +
                "money:" + curListData.getMoney() + ";";
        String textToPrint = String.format(Locale.ROOT, demoStr, 1);

        // 1) UTF-8 text .  Not available send esc command with chr 128-255 :(

        String url = "rawbt:" + textToPrint;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        sendToPrint(intent);
        button.setText("print");
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(hideSystemBars());
//        }
//    }

    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
    }


    public void buildRecyclerView() {

        myListData = MyData.CreatData();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //Sending reference and data to Adapter
        MyListAdapter adapter = new MyListAdapter(myListData);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        //Set
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems(myListData);
            }
        });
        ImageButton refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RearrangeItems(myListData);
            }
        });
    }
    private void RearrangeItems(ArrayList<MyListData> myListData) {
        // Shuffling the data of ArrayList using system time
        Collections.shuffle(myListData, new Random(System.currentTimeMillis()));
        MyListAdapter adapter = new MyListAdapter(myListData);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh(View view) {
        //RearrangeItems();
    }
}