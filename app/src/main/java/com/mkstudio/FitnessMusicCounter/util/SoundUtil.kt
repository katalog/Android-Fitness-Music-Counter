package com.mkstudio.FitnessMusicCounter.util

import android.media.AudioManager
import android.media.ToneGenerator

object SoundUtil {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playShortBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun playHighBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun playFinishBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 400)
        } catch (e: Exception) {
            // Ignore
        }
    }
}
