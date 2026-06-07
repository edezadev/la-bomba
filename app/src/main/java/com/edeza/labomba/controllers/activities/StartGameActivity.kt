package com.edeza.labomba.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.edeza.labomba.R
import com.edeza.labomba.controllers.dialogs.SelectLoserBottomSheet
import com.edeza.labomba.databinding.ActivityStartGameBinding
import com.edeza.labomba.utils.BaseActivity
import com.edeza.labomba.utils.GameSession
import com.edeza.labomba.utils.listeners.OnLoserListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale
import androidx.core.net.toUri
import com.edeza.labomba.models.LoserModel
import com.edeza.labomba.utils.AdsManager
import com.edeza.labomba.utils.Constants
import com.edeza.labomba.utils.setupInsets
import com.google.android.material.appbar.AppBarLayout

class StartGameActivity : BaseActivity(), OnLoserListener {
    private var binding: ActivityStartGameBinding? = null
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvTime: TextView
    private lateinit var btnPlayPause: MaterialButton
    private lateinit var tvTopicName: TextView
    private var round = 0
    private var timeMilliseconds: Long? = 0
    private lateinit var exoPlayer: ExoPlayer
    private var isPlaying = false
    private lateinit var mediaItem: MediaItem
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        AdsManager.init(this) //Inicializar la carga del anuncio
        binding?.let {
            appBarLayout = it.appBarLayout
            toolbar = it.toolbar
            tvTime = it.tvTime
            btnPlayPause = it.btnPlayPause
            tvTopicName = it.tvTopicName
        }
        appBarLayout.setupInsets()
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.primary))
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exit -> {
                    showAlertExitGame()
                    true
                }
                else -> false
            }
        }

//      Inicializa el reproductor
        exoPlayer = ExoPlayer.Builder(this).build()

        btnPlayPause.setOnClickListener { playPause() }

        updateTitleAndTopics()
        setTimerSound()
        createLoserList()
    }

    private fun playPause() {
        if (isPlaying) {
            exoPlayer.pause()
            pause()
            isPlaying = false
        } else {
            exoPlayer.play()
            play()
            isPlaying = true
        }
    }

    private fun updateTitleAndTopics() {
        val titleRounds = getString(R.string.titleRounds)
        toolbar.title = "$titleRounds ${round + 1}"
        tvTopicName.text = GameSession.topics[round].name
    }

//  Cargar un archivo de audio a exoPlayer y convertir tiempo a milisegundos
    private fun setTimerSound() {
        val soundMap = mapOf(
            45000 to R.raw.forty_five_sec,
            60000 to R.raw.minute,
            90000 to R.raw.minute_thirty_sec
        )
        val resourceId = soundMap[GameSession.time] ?: R.raw.thirty_sec

        mediaItem = MediaItem.fromUri("${Constants.SCHEME_ANDROID_RESOURCE}$packageName/$resourceId".toUri())
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        timeMilliseconds = GameSession.time?.toLong()

        updateTimer()
    }
//Crear lista para la selección del perdedor
    private fun createLoserList() {
        for (item in GameSession.players) {
            GameSession.loser.add(LoserModel(item, 0))
        }
    }

    private fun updateTimer() {
        val minutes = (timeMilliseconds?.div(60000))?.toInt()
        val seconds = ((timeMilliseconds?.rem(60000))?.div(1000))?.toInt()

        val textTimer = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds)
        tvTime.text = textTimer
    }

    private fun play() {
        timer = object : CountDownTimer(timeMilliseconds!!, 1000) {
            override fun onTick(p0: Long) {
                timeMilliseconds = p0 //Actualizando el tiempo en milisegundos
                updateTimer() //Actualizando el texto del timer
            }

            override fun onFinish() {
                isPlaying = false
                btnPlayPause.icon = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_play)
                tvTime.text = getString(R.string.text_init_timer)

//              Delay de 1.5 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                  showSelectLoser()
                }, 1500)
            }
        }.start()

        btnPlayPause.icon = AppCompatResources.getDrawable(this, R.drawable.ic_pause)
    }

    private fun showSelectLoser() {
        val bottomSheet= SelectLoserBottomSheet()
        bottomSheet.setOnLoserListener(this)
        bottomSheet.show(supportFragmentManager, SelectLoserBottomSheet.TAG)
    }

    private fun pause() {
        timer.cancel()
        btnPlayPause.icon = AppCompatResources.getDrawable(this, R.drawable.ic_play)
    }

    private fun showAlertExitGame() {
        emergencyPause()

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_alert))
            .setMessage(getString(R.string.message_exit_game))
            .setPositiveButton(getString(R.string.exit)) { dialog, _ ->
                AdsManager.showAd(this) {
                    GameSession.reset()
                    dialog.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
//                    Aegrgar flag para eliminar pila de fragmentos/activities
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.action_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun emergencyPause() {
        if (isPlaying) {
            exoPlayer.pause()
            pause()
            isPlaying = false
        }
    }

    override fun onBottomSheetDismissed() {
        round += 1 //Incrementa la ronda para cambiar el título y tema

//        Valida si aún quedan rondas, en base a la cantidad de temas
        if (round < GameSession.topics.size) {
            timeMilliseconds = GameSession.time?.toLong()
            prepareNextRound() //Prepara la siguiente ronda
        } else {
//            Fin del juego
            AdsManager.showAd(this) {
                startActivity(Intent(this, ResultsActivity::class.java))
                finish()
            }
        }
    }

    private fun prepareNextRound() {
//        Actualiza la UI
        updateTitleAndTopics()
        updateTimer()

//        Evita que exoplayer se reproduzca automaticamente cuando esté listo
        exoPlayer.playWhenReady = false
        exoPlayer.seekTo(0) //Regresa el audio al inicio
        exoPlayer.prepare()

        isPlaying = false
        btnPlayPause.icon = AppCompatResources.getDrawable(this, R.drawable.ic_play)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showAlertExitGame()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    /* Pausar cuando se minimice la app*/
    override fun onPause() {
        super.onPause()
        emergencyPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        exoPlayer.release()
    }
}