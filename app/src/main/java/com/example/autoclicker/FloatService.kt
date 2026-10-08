package com.example.autoclicker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FloatService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var view: View

    override fun onBind(i: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val ch = "ch_id"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(ch, "clicker", NotificationManager.IMPORTANCE_LOW)
            )
        }
        startForeground(1, Notification.Builder(this, ch).setContentTitle("点击器运行中").setSmallIcon(android.R.drawable.ic_menu_help).build())

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) LayoutParams.TYPE_APPLICATION_OVERLAY else LayoutParams.TYPE_PHONE,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        view = LayoutInflater.from(this).inflate(R.layout.float_window, null)
        wm.addView(view, params)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PointAdapter()
        AppState.onListChanged = { (rv.adapter as PointAdapter).notifyDataSetChanged() }

        view.findViewById<Button>(R.id.btnRecord).setOnClickListener {
            AppState.isRecording = !AppState.isRecording
            if (AppState.isRecording) {
                AppState.points.clear(); AppState.lastTime = 0
                Toast.makeText(this, "录制中，请去其他App点击", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "录制完成：${AppState.points.size}个点", Toast.LENGTH_SHORT).show()
            }
            AppState.onListChanged?.invoke()
        }
        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            AppState.points.add(PointConfig(500, 500))
            AppState.onListChanged?.invoke()
        }
        view.findViewById<Button>(R.id.btnPlay).setOnClickListener {
            MyAccessibilityService.instance?.startSmartPlay()
        }
        view.findViewById<Button>(R.id.btnStop).setOnClickListener {
            AppState.isPlaying = false; AppState.isRecording = false
            Toast.makeText(this, "已停止", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::wm.isInitialized) wm.removeView(view)
        super.onDestroy()
    }
}
