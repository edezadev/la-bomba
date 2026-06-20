package com.edeza.labomba.utils

import android.app.Activity
import android.content.Context
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
    private var currentAdUnitId: String? = null //Guarda qué bloque se cargó

    /** Carga un anuncio específico
     * @param adUnitId el ID del bloque (ej: BuildConfig.ID_ADS_INSTRUCTIONS
     */
    fun loadAd(context: Context, adUnitId: String) {
        if (isLoading || (interstitialAd != null && currentAdUnitId == adUnitId)) return

        isLoading = true
        currentAdUnitId = adUnitId
        InterstitialAd.load(
            context.applicationContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Logger.debug("AdsManager", "Ad loaded for ID: $adUnitId")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    currentAdUnitId = null
                    Logger.error("AdsManager", "Ad failed to load: ${error.message}")
                }
            }
        )
    }

    /** Muestra el anuncio cargado
     * @param adUnitId Se pasa para recargar el mismo bloque tras cerrarse
     */
    fun showAd(activity: Activity, adUnitId: String, onFinish: () -> Unit) {
        val ad = interstitialAd

        // Si no hay anuncio listo o el ID no coincide, continuar sin anuncio
        if (ad == null || currentAdUnitId != adUnitId) {
            onFinish()
            // Cargar el anuncio correcto para la próxima vez
            loadAd(activity, adUnitId)
            return
        }

        ad.fullScreenContentCallback =
            object : FullScreenContentCallback() { //Callback indica qué acción se toma con el anuncio
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    currentAdUnitId = null
                    onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    currentAdUnitId = null
                    onFinish()
                }
            }
        ad.show(activity)
    }
}