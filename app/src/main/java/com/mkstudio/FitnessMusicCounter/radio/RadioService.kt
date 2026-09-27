package com.mkstudio.FitnessMusicCounter.radio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.mkstudio.FitnessMusicCounter.R
import com.mkstudio.FitnessMusicCounter.ui.MainActivity
import com.mkstudio.FitnessMusicCounter.util.Constants
import com.mkstudio.FitnessMusicCounter.util.myLogD
import com.mkstudio.FitnessMusicCounter.util.myLogE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RadioService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null
    private val myBinder: IBinder = MyLocalBinder()

    companion object {
        private const val NOTIFICATION_ID = 1010
        private const val CHANNEL_ID = "fitness_radio_channel"

        private val _currentStation = MutableStateFlow(Constants.STR_NO_RADIO)
        val currentStation: StateFlow<String> = _currentStation.asStateFlow()

        private val _isPlaying = MutableStateFlow(false)
        val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setUserAgent("FitnessMusicCounter/1.3 (Android)")

        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(httpDataSourceFactory)

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        myLogD("Player onIsPlayingChanged: $playing")
                        _isPlaying.value = playing
                        updateNotification(currentStation.value, playing)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> myLogD("Player STATE_BUFFERING")
                            Player.STATE_READY -> myLogD("Player STATE_READY")
                            Player.STATE_ENDED -> {
                                myLogD("Player STATE_ENDED")
                                _isPlaying.value = false
                            }
                            Player.STATE_IDLE -> {
                                myLogD("Player STATE_IDLE")
                                _isPlaying.value = false
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        myLogE("Player error: ${error.errorCodeName} - ${error.message}")
                        _isPlaying.value = false
                    }
                })
            }

        mediaSession = MediaSession.Builder(this, player!!).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onBind(intent: Intent?): IBinder? {
        if (intent?.action == "androidx.media3.session.MediaSessionService") {
            return super.onBind(intent)
        }
        return myBinder
    }

    fun playRadio(strStationName: String) {
        val strStationAddr = Constants.radioStationMap[strStationName] ?: return
        myLogD("playRadio: station = $strStationName, url = $strStationAddr")
        _currentStation.value = strStationName

        player?.apply {
            stop()
            clearMediaItems()
            val mediaItem = MediaItem.Builder()
                .setUri(strStationAddr)
                .build()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            play()
        }

        startForegroundServiceNotification(strStationName)
    }

    fun togglePlay() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun stopRadio() {
        player?.stop()
        _isPlaying.value = false
        _currentStation.value = Constants.STR_NO_RADIO
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun startForegroundServiceNotification(stationName: String) {
        val notification = buildNotification(stationName, true)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(stationName: String, playing: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(stationName, playing))
    }

    private fun buildNotification(stationName: String, playing: Boolean): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Fitness Music Counter")
            .setContentText("Radio: $stationName ${if (playing) "▶ Playing" else "⏸ Paused"}")
            .setContentIntent(pendingIntent)
            .setOngoing(playing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fitness Radio Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls background radio playback"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    inner class MyLocalBinder : Binder() {
        fun getService(): RadioService = this@RadioService
    }

    override fun onDestroy() {
        player?.stop()
        player?.release()
        player = null
        mediaSession?.release()
        mediaSession = null
        _isPlaying.value = false
        _currentStation.value = Constants.STR_NO_RADIO
        super.onDestroy()
    }
}