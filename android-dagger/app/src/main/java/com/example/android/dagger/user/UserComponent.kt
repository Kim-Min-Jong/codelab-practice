package com.example.android.dagger.user

import com.example.android.dagger.main.MainActivity
import com.example.android.dagger.settings.SettingsActivity
import dagger.Subcomponent


// 이제 MainActivity와 SettingsActivity 두 군데서 사용되기 떄문에 인스턴스 접근? 생성 문제가 생김
// 앞서 봤던 것 중 @Singleton을 적용할 수도 있지만
// user flow에서 login out / register는 다른 flow이기 때문에 같은 인스턴스를 사용하지 않길 원함
// 그래서 UserData도 SubComponent로 빼는 작업이 필요함

@LoggedUserScope
@Subcomponent
interface UserComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): UserComponent
    }

    fun inject(activity: SettingsActivity)
    fun inject(activity: MainActivity)
}
