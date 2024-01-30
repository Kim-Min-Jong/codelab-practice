package com.example.android.dagger.user

import javax.inject.Scope

// UserDataRepository - UserComponent는 MainActivity와 SettingsActivity가 같은 인스턴스를 공유할 수 있다.
// 액티비티 생명주기를 관리하기 위해 @ActivityScope를 붙여 사용했는데
// 앱 전반이 아니라 여러 개의 액티비티를 커버할 수 있는 스코프가 필요하다 (로그인 된 후의 액티비티 전환?)
// 그래서 LoggedUserScope를 만들어 로그인이 됐을 떄의 전반적인 생명주기를 관리한다.

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class LoggedUserScope
