package com.example.activity_embedding

import android.content.Context
import androidx.startup.Initializer
import androidx.window.embedding.RuleController

// 규칙에 따른 화면 분할을 도와 주는 클래스
class SplitInitializer: Initializer<RuleController> {
    // 룰을 만드는 팩토리 메소드
    override fun create(context: Context): RuleController {
        // 룰 객체를 생성
        return RuleController.getInstance(context).apply {
            // main_split_config에서 정의한 룰을 가저와 세팅
            setRules(RuleController.parseRules(context, R.xml.main_split_config))
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
