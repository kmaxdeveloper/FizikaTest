package uz.kmax.fizikatest.tools.manager

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdsManager2() {

    lateinit var adRequest: AdRequest
    private var onAdDismissClickListener : (()-> Unit)? = null
    fun setOnAdDismissClickListener(f: ()-> Unit){ onAdDismissClickListener = f }

    private var onAdsClickedListener : (()-> Unit)? = null
    fun setOnAdsClickListener(f: ()-> Unit){ onAdsClickedListener = f }

    private var onAdsNotReadyListener : (()-> Unit)? = null
    fun setOnAdsNotReadyListener(f: ()-> Unit){ onAdsNotReadyListener = f }

    private var onAdsNullOrNonNullListener : ((status : Boolean)-> Unit)? = null
    fun setOnAdsNullListener(f : (status : Boolean)-> Unit){
        onAdsNullOrNonNullListener = f
    }

    private var mInterstitialAd: InterstitialAd? = null

    fun initialize(context: Context){
        MobileAds.initialize(context) {}
        adRequest = AdRequest.Builder().build()
    }

    fun initializeInterstitialAds(context: Context, adUnit : String){
        InterstitialAd.load(context,adUnit, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                onAdsNullOrNonNullListener?.invoke(false)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                onAdsNullOrNonNullListener?.invoke(true)
                callbackInterstitialAds()
            }
        })
    }

    private fun callbackInterstitialAds(){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                onAdsClickedListener?.invoke()
            }
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                onAdDismissClickListener?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdImpression() {}
            override fun onAdShowedFullScreenContent() {}
        }
    }

    fun showInterstitialAds(activity: Activity){
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            onAdsNotReadyListener?.invoke()
        }
    }

    fun initializeBanner(adView: AdView){
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {}
            override fun onAdClosed() {}
            override fun onAdFailedToLoad(adError : LoadAdError) {}
            override fun onAdImpression() {}
            override fun onAdLoaded() {}
            override fun onAdOpened() {}
        }
    }
}