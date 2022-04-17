import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ContextAndBuilders {

    fun logSomething() = runBlocking(CoroutineName("main_coroutine")) {
        log("Started") // [main_coroutine] Started


        val v1 = async {
            delay(500)
            log("Running async!") // [main_coroutine] Running async!
            42
        }

        launch {
            delay(2000)
            log("Running launch!") // [main_coroutine] Running launch!
        }

        log("The answer is: ${v1.await()}") // [main_coroutine] The answer is: 42
    }

    fun logSomethingAndOverrideChildContext() = runBlocking(CoroutineName("main_coroutine")) {
        log("Started") // [main_coroutine] Started


        val v1 = async(CoroutineName("C1")) {
            delay(500)
            log("Running async!") // [main_coroutine] Running async!
            42
        }

        launch(CoroutineName("C2")) {
            delay(2000)
            log("Running launch!") // [main_coroutine] Running launch!
        }

        log("The answer is: ${v1.await()}") // [main_coroutine] The answer is: 42
    }

    fun contextInSuspendingFunction() = runBlocking(CoroutineName("Outer")) {
        printName() // Outer
        launch(CoroutineName("Inner")) {
            printName()
        }
        delay(10)
        printName()  // Outer
    }

    fun tryCustomContext() = runBlocking(CounterContext("Outer")) {
        printNext() // Outer: 0
        launch {
            printNext() // Outer: 1

            launch(CounterContext("Inner")) {
                printNext() // Inner: 0
                printNext() // Inner: 1

                launch {
                    printNext() // Inner: 2
                }
            }
        }
        printNext() // Outer: 2
    }

    private suspend fun printName() {
        println(coroutineContext[CoroutineName]?.name)
    }

    private fun CoroutineScope.log(msg: String) {
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] $msg")
    }

    private suspend fun printNext() {
        coroutineContext[CounterContext]?.printNext()
    }
}