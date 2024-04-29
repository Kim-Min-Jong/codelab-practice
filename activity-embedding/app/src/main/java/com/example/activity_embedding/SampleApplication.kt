package com.example.activity_embedding

import android.app.Application
import androidx.window.embedding.RuleController

// 여기서 화면 분할 룰을 초기화(등록)
class SampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        // 등록
        RuleController.getInstance(this)
            .setRules(RuleController.parseRules(this, R.xml.main_split_config))
//        SplitInitializer().create(this)
    }
}
