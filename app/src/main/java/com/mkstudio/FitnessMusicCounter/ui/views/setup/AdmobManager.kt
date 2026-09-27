package com.mkstudio.FitnessMusicCounter.ui.views.setup

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mkstudio.FitnessMusicCounter.BuildConfig
import com.mkstudio.FitnessMusicCounter.repo.LocalPreferences
import com.mkstudio.FitnessMusicCounter.repo.MainRepository
import com.mkstudio.FitnessMusicCounter.ui.MainActivity
import com.mkstudio.FitnessMusicCounter.util.myLogD
import com.mkstudio.FitnessMusicCounter.util.myLogE
import com.mkstudio.FitnessMusicCounter.util.myLogW
import javax.inject.Inject

class AdmobManager(val activity: Activity, val repo:MainRepository) {
    // admob
    private var mInterstitialAd: InterstitialAd? = null
    var strADsId = BuildConfig.FULL_ADS_KEY
    val con = activity as Context

    private var admobPrefareListener: (v:Boolean) -> Unit = {}

    fun setPrefareListener(callback: (v:Boolean) -> Unit) {
        admobPrefareListener = callback
    }

    fun prefare() {
        var adRequest = AdRequest.Builder().build()

        // count + stat buttons are disable before load ads
        admobPrefareListener.invoke(false)

        InterstitialAd.load(con, strADsId,
            adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    myLogE(adError!!.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    myLogD("Ad was loaded.")
                    mInterstitialAd = interstitialAd

                    // count + stat buttons are enable if admob loaded
                    admobPrefareListener.invoke(true)
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                myLogD("Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                myLogE("Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                myLogD("Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
    }

    fun show(): Boolean {
        if (mInterstitialAd == null) {
            myLogD("The interstitial ad wasn't ready yet.")
            return true
        }

        if ( BuildConfig.DEBUG ) {
            mInterstitialAd?.show(activity)
        } else {
            if (repo.isShowAdmobToday() == false) {
                repo.keepShowAdmobToday()
                mInterstitialAd?.show(activity)
                return true
            } else {
                myLogW("already show admob today!")
            }
        }

        return false
    }
}