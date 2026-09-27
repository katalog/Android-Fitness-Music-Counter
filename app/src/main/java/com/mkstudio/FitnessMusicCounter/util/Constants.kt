package com.mkstudio.FitnessMusicCounter.util

object Constants {
    const val APPLICATION_ID = "com.mkstudio.FitnessMusicCounter"

    val radioStationMap = mapOf(
        "NRJ_Fitness"   to "https://frontend.streamonkey.net/energy-fitness",
        "NRJ_Dance"     to "https://frontend.streamonkey.net/energy-dance/stream/mp3",
        "NRJ_Berlin"    to "https://frontend.streamonkey.net/energy-berlin",
        "NRJ_HitRemix"  to "https://streaming.nrjaudio.fm/oug77irb92oc",
        "NRJ_PartyHits" to "https://frontend.streamonkey.net/energy-partyhits",
        "NRJ_Hit2000"   to "https://frontend.streamonkey.net/energy-2000erhits"
    )

    const val KEY_REPS = "key_reps"
    const val KEY_IS_FIRST_RUN = "key_isfirstrun"

    const val STR_NO_RADIO = "NO RADIO"
    const val STR_SELECT_RADIO_STATION = "Select Radio Station"
    const val STR_INTPUT_REPS_COUNT = "Please input reps count"
    const val STR_DEFUALT_WORKOUT_TIME = "00:00:00"
}
