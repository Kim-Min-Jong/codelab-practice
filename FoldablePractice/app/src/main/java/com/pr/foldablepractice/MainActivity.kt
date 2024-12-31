package com.pr.foldablepractice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import com.pr.foldablepractice.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        // DisplayFeatures 및 경계, 레이아웃 변경사항 수신
        // 변경사항을 수신해야하기에 lifecycle에 따라 동작해야하므로 coroutine 이 필요
        onWindowLayoutInfoChange()
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

    private fun onWindowLayoutInfoChange() {
        // lifecycle.coroutineScope 또는 lifecycleOwner.lifecycleScope 속성을 통해 수명 주기의 코루틴 범위에 액세스
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(this@MainActivity)
                    .collect {
                        updateUI(it)
                    }
            }
        }
    }

    // flow에서 수집한 정보를 출력
    private fun updateUI(newLayoutInfo: WindowLayoutInfo) {
        binding.layoutChange.text = newLayoutInfo.toString()
        // WindowLayoutInfo 데이터에 디스플레이 기능이 있는지 확인
        if (newLayoutInfo.displayFeatures.isNotEmpty()) { // 접이식 or 힌지 (논리 디스플레이를 전부 차지해야함)
            binding.configurationChanged.text = "Spanned across displays"
        } else { // 디스플레이 기능이 없다면 앱은 단일 화면 기기나 모드 또는 멀티 윈도우 모드에서 실행
            binding.configurationChanged.text = "One logic/physical display - unspanned"
        }
    }
}

