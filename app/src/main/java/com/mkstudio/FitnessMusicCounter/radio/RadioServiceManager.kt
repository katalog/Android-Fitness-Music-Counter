package com.mkstudio.FitnessMusicCounter.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.mkstudio.FitnessMusicCounter.util.myLogD
import com.mkstudio.FitnessMusicCounter.util.myLogW
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioServiceManager @Inject constructor(@ApplicationContext private val con: Context) {
    private var myService: RadioService? = null
    private var isBind = false
    private var pendingStation: String? = null

    val currentStation: StateFlow<String> = RadioService.currentStation
    val isPlaying: StateFlow<Boolean> = RadioService.isPlaying

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            myLogD("RadioService onServiceConnected")
            if (p1 is RadioService.MyLocalBinder) {
                myService = p1.getService()
                isBind = true

                pendingStation?.let { station ->
                    myLogD("Executing pending station playback: $station")
                    myService?.playRadio(station)
                    pendingStation = null
                }
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            myLogW("RadioService onServiceDisconnected")
            myService = null
            isBind = false
        }
    }

    fun playRadio(stationName: String) {
        myLogD("RadioServiceManager playRadio: $stationName (isBind=$isBind, myService=$myService)")
        if (myService != null && isBind) {
            myService?.playRadio(stationName)
        } else {
            pendingStation = stationName
            val intent = Intent(con, RadioService::class.java)
            try {
                con.startService(intent)
            } catch (e: Exception) {
                myLogW("startService error: ${e.message}")
            }
            con.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun togglePlay() {
        myService?.togglePlay()
    }

    fun stopRadio() {
        myService?.stopRadio()
    }

    fun isBind(): Boolean = isBind

    fun bindRadio() {
        if (!isBind) {
            val intent = Intent(con, RadioService::class.java)
            con.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindRadio() {
        if (isBind) {
            try {
                con.unbindService(connection)
            } catch (e: Exception) {
                // Ignore if not registered
            }
            isBind = false
            myService = null
        }
    }
}