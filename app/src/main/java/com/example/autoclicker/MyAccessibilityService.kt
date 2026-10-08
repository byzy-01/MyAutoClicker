package com.example.autoclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import java.util.Random

class MyAccessibilityService : AccessibilityService() {
    companion object { var instance: MyAccessibilityService? = null }
    private val rand = Random()

    // 安全随机偏移计算（防检测，避免负数范围崩溃）
    private fun rnd(offset: Int): Int = if (offset <= 0) 0 else rand.nextInt(offset * 2 + 1) - offset

    override fun onServiceConnected() { instance = this; super.onServiceConnected() }
    override fun onDestroy() { instance = null; super.onDestroy() }
    override fun onInterrupt() {}

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        if (e == null || e.packageName == packageName) return
        if (AppState.isRecording && e.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            e.source?.let {
                val r = Rect(); it.getBoundsInScreen(r)
                if (!r.isEmpty) {
                    val now = System.currentTimeMillis()
                    val d = if (AppState.lastTime == 0L) 0L else now - AppState.lastTime
                    AppState.lastTime = now
                    AppState.points.add(PointConfig(r.centerX(), r.centerY(), d, 50, 1, 5))
                    AppState.onListChanged?.invoke()
                }
            }
        }
    }

    // 核心播放引擎：多点位 + 自定义时长/次数 + 随机防检测
    fun startSmartPlay() {
        if (AppState.isPlaying || AppState.points.isEmpty()) return
        AppState.isPlaying = true
        Thread {
            for (p in AppState.points) {
                repeat(p.repeatCount) {
                    if (!AppState.isPlaying) return@Thread
                    val jx = p.x + rnd(p.randomOffset)
                    val jy = p.y + rnd(p.randomOffset)
                    val jd = (p.delayBefore + rnd(50)).coerceAtLeast(0)
                    Thread.sleep(jd)

                    val path = Path().apply { moveTo(jx.toFloat(), jy.toFloat()) }
                    val gesture = GestureDescription.Builder()
                        .addStroke(GestureDescription.StrokeDescription(path, 0, p.pressDuration))
                        .build()
                    Handler(Looper.getMainLooper()).post { dispatchGesture(gesture, null, null) }
                    Thread.sleep(p.pressDuration + 20)
                }
            }
            AppState.isPlaying = false
        }.start()
    }
}
