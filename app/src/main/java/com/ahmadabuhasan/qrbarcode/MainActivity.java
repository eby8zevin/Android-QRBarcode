package com.ahmadabuhasan.qrbarcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmadabuhasan.qrbarcode.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
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
    private static long back_pressed;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }

        binding.flashOff.setOnClickListener(v -> {
            binding.flashOff.setVisibility(View.GONE);
            binding.flashOn.setVisibility(View.VISIBLE);

            flashlight = !flashlight;
            zXingScannerView.setFlash(flashlight);
        });

        binding.flashOn.setOnClickListener(v -> {
            binding.flashOff.setVisibility(View.VISIBLE);
            binding.flashOn.setVisibility(View.GONE);

            flashlight = !flashlight;
            zXingScannerView.setFlash(flashlight);
        });

        MobileAds.initialize(this, initializationStatus -> {

        });
        new Utils().interstitialAdsShow(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adViewCamera.loadAd(adRequest);

        zXingScannerView = new ZXingScannerView(this);
        binding.contentFrame.addView(zXingScannerView);
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
        binding = null;
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, rawResult.toString(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, rawResult.toString());
        startActivity(Intent.createChooser(intent, "Share via"));
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
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
        if (back_pressed + 3000 > System.currentTimeMillis()) {
            finishAffinity();
            if (Utils.interstitialAd.isLoaded()) {
                Utils.interstitialAd.show();
            } else {
                super.onBackPressed();
                finishAndRemoveTask();
            }
        } else {
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}