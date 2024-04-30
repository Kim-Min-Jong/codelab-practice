package com.example.activity_embedding.util

import android.content.ComponentName
import android.content.Context
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitPairFilter
import androidx.window.embedding.SplitPairRule
import androidx.window.embedding.SplitRule
import com.example.activity_embedding.view.DetailActivity
import com.example.activity_embedding.view.ListActivity

// ListActivity에서 호출할 객체
class SplitManager {

    companion object {
        fun createSplit(context: Context) {
            // ListActivity 및 DetailActivity 분할하는 분할 쌍 필터 생성
            val splitPairFilter = SplitPairFilter(
                ComponentName(context, ListActivity::class.java),
                ComponentName(context, DetailActivity::class.java),
                null
            )

            val filterSet = setOf(splitPairFilter)

            // 분할의 레이아웃 속성 생성
            val splitAttributes = SplitAttributes.Builder()
                .setSplitType(SplitAttributes.SplitType.ratio(0.3f))
                .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
                .build()

            // 분할 규칙 생성
            val splitPairRule = SplitPairRule.Builder(filterSet)
                // 규칙에 레이아웃 속성 추가
                .setDefaultSplitAttributes(splitAttributes)
                // 분할을 허용하는 최소 디스플레이 너비
                .setMinWidthDp(840)
                // 분할을 허용하기 위해 기기 방향과 관계없이 두 디스플레이 크기 중 더 작은 값이 취해야 할 최솟값(dp)
                .setMinSmallestWidthDp(600)
                // 보조 컨테이너의 모든 액티비티가 종료될 때 기본 컨테이너의 액티비티가 어떠한 영향을 미치는지 설정
                // NEVER는 보조 컨테이너의 모든 액티비티가 종료될 때 기본 액티비티가 종료되어서는 안 됨을 나타냄
                .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.NEVER)
                // 위와 반대 개념  보조 <-> 기본
                .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
                // 컨테이너에서 새 액티비티가 실행될 때 보조 컨테이너의 모든 액티비티가 종료되는지를 지정
                .setClearTop(false)
                .build()

            val ruleController = RuleController.getInstance(context).apply {
                addRule(splitPairRule)
            }

        }
    }

}
