import kotlinx.coroutines.*
import kotlin.time.measureTime

fun main() {
    // 수행 시간을 재볼 수도 있음
    val time = measureTime {
        // runBlocing은 동기적으로 동작
        // 람다 내 모든 작업이 완료될 때까지는 반환되지 않음
        // delay 호출의 작업이 완료될때까지 종료하지 않음
        runBlocking {
            println("Weather forecast")
            // suspend function은 코루틴 스코프내에서만 실행가능 -- 컴파일 오류
            // 1초씩 지연되며 실행
            printForecast()
            printTemperature()
        }
    }

    println("Execution time: ${time / 1000.0} seconds")
}
suspend fun printForecast() {
    delay(1000)
    println("Sunny")
}

suspend fun printTemperature() {
    delay(1000)
    println("30\u00b0C")
}