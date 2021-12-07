package com.cuse.myandroidpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;

import com.cuse.myandroidpos.databinding.ActivityItemDetailBinding;
import com.cuse.myandroidpos.databinding.FragmentItemDetailBinding;
import com.cuse.myandroidpos.databinding.FragmentItemListBinding;
import com.cuse.myandroidpos.databinding.FragmentSignBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityItemDetailBinding binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding.getRoot() -> app:id/container -> activity_item_detail

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_item);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.
                Builder(navController.getGraph())
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_item);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}