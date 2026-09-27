package com.mkstudio.FitnessMusicCounter.model

enum class WorkoutMode(val id: Int, val title: String, val subtitle: String) {
    TABATA(1, "타바타 / HIIT", "운동 & 휴식 인터벌 자동 반복"),
    REST_TIMER(2, "자동 휴식 타이머", "세트 완료 시 휴식 카운트다운"),
    MANUAL(3, "수동 카운터", "자유로운 세트별 랩타임 기록")
}

enum class TabataPhase {
    PREPARE,
    WORK,
    REST,
    FINISHED
}

data class WorkoutConfig(
    val mode: WorkoutMode = WorkoutMode.REST_TIMER,
    val targetSets: Int = 10,
    val restDurationSeconds: Int = 60,
    val workDurationSeconds: Int = 20
)
