package com.example.autoclicker

data class PointConfig(
    var x: Int, var y: Int,
    var delayBefore: Long = 1000, var pressDuration: Long = 50,
    var repeatCount: Int = 1, var randomOffset: Int = 5
)

object AppState {
    var isRecording = false
    var isPlaying = false
    val points = mutableListOf<PointConfig>()
    var lastTime = 0L
    var onListChanged: (() -> Unit)? = null
}
