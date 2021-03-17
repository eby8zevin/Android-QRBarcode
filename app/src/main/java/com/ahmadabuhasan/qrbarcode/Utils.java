package com.ahmadabuhasan.qrbarcode;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Utils {

    public static InterstitialAd interstitialAd;

    public void interstitialAdsShow(Context context) {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.AdMob_Interstitial_Ads_ID));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                AdsDelay();
                Log.d("interstitialAd", "Show");
            }
        });
    }

    public void AdsDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (interstitialAd == null || !interstitialAd.isLoaded()) {
                return;
            }
            interstitialAd.show();
        }, 30000);
    }
}