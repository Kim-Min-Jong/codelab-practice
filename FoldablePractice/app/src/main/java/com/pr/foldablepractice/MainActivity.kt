package com.pr.foldablepractice

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
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
            //  앱이 스팬되는 런타임에 현재 보유한 UI/UX를 조정하여 중요한 정보가 디스플레이 기능
            alignViewToFoldingFeatureBounds(newLayoutInfo)
        } else { // 디스플레이 기능이 없다면 앱은 단일 화면 기기나 모드 또는 멀티 윈도우 모드에서 실행
            binding.configurationChanged.text = "One logic/physical display - unspanned"
        }
    }

    private fun alignViewToFoldingFeatureBounds(newLayoutInfo: WindowLayoutInfo) {
        // 뷰의 레이아웃
        val constraintLayout = binding.constraintLayout
        // 제한 조건
        val set = ConstraintSet()
        set.clone(constraintLayout)

        // 현재 뷰의 크기를 foldingFeature 객체로 가져옴
        val foldingFeature = newLayoutInfo.displayFeatures[0] as FoldingFeature
        // 기능 경계를 뷰의 좌표 공간 및 창의 현재 위치로 변환
        val bounds = getFeatrueBoundsInWindow(foldingFeature, binding.root)

        bounds?.let { rect ->
            // 일부 장치에는 0px 너비 접이 기능이 있다.
            // UI에서 접이 기능을 반영하는 뷰를 표시하고 참조로 사용할 수 있도록 최소 1px를 설정했음
            val horizontalFoldingFeatureHeight = (rect.bottom - rect.top).coerceAtLeast(1)
            val verticalFoldingFeatureWidth = (rect.right - rect.left).coerceAtLeast(1)

            // FoldingFeature의 width와 height를 뷰에 맞춤 (제한조건 지정)
            set.constrainHeight(
                R.id.folding_feature,
                horizontalFoldingFeatureHeight
            )
            set.constrainWidth(
                R.id.folding_feature,
                verticalFoldingFeatureWidth
            )

            // 제한조건 연결
            set.connect(
                R.id.folding_feature, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START, 0
            )
            set.connect(
                R.id.folding_feature, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0
            )

            // 화면이 가로모드인지 세로모드인지
            if (foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL) {
                set.setMargin(R.id.folding_feature, ConstraintSet.START, rect.left)
                set.connect(
                    R.id.layout_change, ConstraintSet.END,
                    R.id.folding_feature, ConstraintSet.START, 0
                )
            } else {
                // FoldingFeature is Horizontal
                set.setMargin(
                    R.id.folding_feature, ConstraintSet.TOP,
                    rect.top
                )
                set.connect(
                    R.id.layout_change, ConstraintSet.TOP,
                    R.id.folding_feature, ConstraintSet.BOTTOM, 0
                )
            }

            // Set the view to visible and apply constraints
            set.setVisibility(R.id.folding_feature, View.VISIBLE)
            set.applyTo(constraintLayout)
        }
        }
    }

    private fun getFeatrueBoundsInWindow(
        displayFeature: DisplayFeature,
        view: View,
        includePadding: Boolean = true
    ): Rect? {
        // 화면에서 뷰의 위치를 feature와 동일한 좌표 공간에 조정
        val viewLocationInWindow = IntArray(2)
        view.getLocationInWindow(viewLocationInWindow)

        // 구한 좌표 공간에서 화면의 width, height를 통해 뷰의 범위를 나타내는 Rect 객체 생성
        val viewRect = Rect(
            viewLocationInWindow[0],
            viewLocationInWindow[1],
            viewLocationInWindow[0] + view.width,
            viewLocationInWindow[1] + view.height,
        )

        // 패딩이 있다면 패딩만큼 추가 계산
        if (includePadding) {
            viewRect.left += view.paddingLeft
            viewRect.top += view.paddingTop
            viewRect.right -= view.paddingRight
            viewRect.bottom -= view.paddingBottom
        }

        // Feature의 범위 (Rect)
        val featureRectInView = Rect(displayFeature.bounds)
        // Feature와 view의 교합점 확인
        val intersects = featureRectInView.intersect(viewRect)

        // 디스플레이 Feautre가 View와 겹치는지 확인
        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
            !intersects
        ) {
            return null
        }

        // 특징 좌표를 오프셋하여 좌표 공간 시작점 보기
        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

        return featureRectInView
    }
}

