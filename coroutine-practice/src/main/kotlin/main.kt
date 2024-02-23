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
//            printForecast()
//            printTemperature()

            // 동시에 수행 해보기 위해 새로운 코루틴을 실행 - launch 메소드
//            launch {
//                printForecast()
//            }
//            launch {
//                printTemperature()
//            }

            // 코루틴이 완료되는 시점에 관심이 있고 코루틴의 반환 값이 필요하다면 코루틴 라이브러리의 async() 함수를 사용
            // async는 특정 유형을 반환하므로 리턴에 신경써야함
            // async() 함수는 Deferred 유형의 객체를 반환
            // await()를 사용한 Deferred 객체의 결과에 액세스할 수 있음
            val forecast: Deferred<String> = async {
                printForecast()
            }
            val temperature: Deferred<String> = async {
                printTemperature()
            }

            println("${forecast.await()} ${temperature.await()}")

            // await가 수행 되기 전 까진 수행이 되지 않음
            println("Have a good day!")
        }

    }
    println("Execution time: ${time / 1000.0} seconds")
}
suspend fun printForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun printTemperature(): String {
    delay(1000)
    return "30\u00b0C"
}