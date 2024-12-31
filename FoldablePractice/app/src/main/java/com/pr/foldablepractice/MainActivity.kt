package com.pr.foldablepractice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.window.layout.WindowInfoTracker

class MainActivity : AppCompatActivity() {
    // 윈도우 매니저 변수 선언
    private lateinit var windowInfoTracker: WindowInfoTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // window manager access
        // 현재 기기의 창 상태의 정보 상태가 담김
        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)
    }
}
