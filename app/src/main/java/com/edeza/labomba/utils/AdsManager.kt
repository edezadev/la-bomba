package com.edeza.labomba.utils

import android.app.Activity
import android.content.Context
import com.edeza.labomba.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
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
        isLoading = true //Marca que hay una petición activa
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad //Guarda el anuncio listo
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun showAd(activity: Activity, onFinish: () -> Unit) {
        val ad = interstitialAd

//        Si no hay anuncio la app sigue normal, nunca se bloquea el flujo del juego
        if (ad == null) {
            onFinish()
            return
        }

        ad.fullScreenContentCallback =
            object : FullScreenContentCallback() { //Callback indica qué acción se toma con el anuncio
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onFinish()
                }
            }
        ad.show(activity)
    }
}