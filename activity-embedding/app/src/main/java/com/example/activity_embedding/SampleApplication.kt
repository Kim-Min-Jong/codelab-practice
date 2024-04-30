package com.example.activity_embedding

import android.app.Application
import androidx.window.embedding.RuleController
import com.example.activity_embedding.R
import com.example.activity_embedding.util.SplitManager

// 여기서 화면 분할 룰을 초기화(등록)
class SampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        // 등록 (xml을 통해서)
//        RuleController.getInstance(this)
//            .setRules(RuleController.parseRules(this, R.xml.main_split_config))

        // 객체 분리해서 등록 (직접 생성 X) (코드를 통해서 생성 - SplitManager)
        SplitManager.createSplit(this)
    }
}
