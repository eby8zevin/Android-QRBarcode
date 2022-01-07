package com.ahmadabuhasan.qrbarcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmadabuhasan.qrbarcode.databinding.ActivityAboutBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("About");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.version.setText(String.format("%s%s", getResources().getString(R.string.version), BuildConfig.VERSION_NAME));

        MobileAds.initialize(this, initializationStatus -> {

        });
        new Utils().interstitialAdsShow(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adViewAbout.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent back = new Intent(this, MainActivity.class);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(back);
        return true;
    }
}