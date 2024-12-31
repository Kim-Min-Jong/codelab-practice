package com.pr.foldablepractice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import com.pr.foldablepractice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // 윈도우 매니저 변수 선언
    private lateinit var windowInfoTracker: WindowInfoTracker

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window manager access
        // 현재 기기의 창 상태의 정보 상태가 담김
        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)

        // windowMetrics 정보를 가져오는 메소드
        obtainWindowMetrics()
    }

    private fun obtainWindowMetrics() {
        //  WindowMetricsCalculator 객체를 가져옴
        val wmc = WindowMetricsCalculator.getOrCreate()
        // 현재 창의 bound 정보 (Rect 객체)를 반환
        // 기기 크기에 맞는 Metrics를 가져옴
        val currentWindowMetrics = wmc.computeCurrentWindowMetrics(this).bounds.flattenToString()
        val maximumWindowMetrics = wmc.computeMaximumWindowMetrics(this).bounds.flattenToString()

        binding.windowMetrics.text =
            "CurrentWindowMetrics: ${currentWindowMetrics}\n" +
            "MaximumWindowMetrics: ${maximumWindowMetrics}"
    }
}
