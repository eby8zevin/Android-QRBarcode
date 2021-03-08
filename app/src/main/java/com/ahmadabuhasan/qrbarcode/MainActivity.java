package com.ahmadabuhasan.qrbarcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Ahmad Abu Hasan on 05/03/2021
 * "com.android.tools.build:gradle:4.1.1"
 */

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int PERMISSION_CODE = 100;
    private static final String FLASH_STATE = "FLASH_STATE";
    private ZXingScannerView zXingScannerView;
    private boolean flashlight;
    ImageView flashOff, flashOn;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }

        flashOff = findViewById(R.id.flashOff);
        flashOn = findViewById(R.id.flashOn);

        flashOff.setOnClickListener(v -> {
            flashOff.setVisibility(View.GONE);
            flashOn.setVisibility(View.VISIBLE);

            flashlight = !flashlight;
            zXingScannerView.setFlash(flashlight);
        });

        flashOn.setOnClickListener(v -> {
            flashOff.setVisibility(View.VISIBLE);
            flashOn.setVisibility(View.GONE);

            flashlight = !flashlight;
            zXingScannerView.setFlash(flashlight);
        });

        MobileAds.initialize(this, initializationStatus -> {

        });
        interstitialAdsShow();

        adView = findViewById(R.id.adViewCamera);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        zXingScannerView = new ZXingScannerView(this);
        contentFrame.addView(zXingScannerView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, flashlight);
    }

    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.setAspectTolerance(0.2f);
        zXingScannerView.startCamera();
        zXingScannerView.setFlash(flashlight);
    }

    public void onDestroy() {
        super.onDestroy();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, rawResult.toString(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, rawResult.toString());
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void interstitialAdsShow() {
        InterstitialAd.load(this, getResources().getString(R.string.AdMob_Interstitial_Ads_ID),
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitial) {
                        interstitialAd = interstitial;
                        Log.d("interstitialAd", "show");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;
                        Log.d("interstitialAd", "failed");
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.optionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return true;
    }

    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finishAffinity();
            if (interstitialAd != null) {
                interstitialAd.show(MainActivity.this);

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAdsShow();
                        MainActivity.super.onBackPressed();
                        Log.d("interstitialAd", "onAdDismissedFullScreenContent: ");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("interstitialAd", "onAdFailedToShowFullScreenContent: ");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        interstitialAd = null;
                        Log.d("interstitialAd", "onAdShowedFullScreenContent: ");
                    }
                });
            } else {
                super.onBackPressed();
            }
        } else {
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}