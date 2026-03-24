package com.example.labombav2.utils

import android.content.Context
import com.example.labombav2.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdsManager {
    private var interstitialAd: InterstitialAd? = null
    /* Bandera de control, garantiza:
    * Solo una carga de anuncio activa a la vez
    * No se recarga si ya hay un anuncio listo
    * Control total del ciclo de vida */
    private var isLoading = false

    fun init(context: Context) {
        loadInterstitial(context)
    }

    private fun loadInterstitial(context: Context) {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            }
        )

    }
}