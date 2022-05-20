import kotlinx.coroutines.*

// (2 sec)
// Will be printed
class NonPropagating {

    suspend fun test() = coroutineScope {
        launch { // 1
            launch { // 2
                delay(2000)
                println("Will not be printed")
            }
            throw MyNonPropagatingException // 3
        }
        launch { // 4
            delay(2000)
            println("Will be printed")
        }
    }

    // Caught java.lang.Error: Some error
    // Will be printed
    //sampleEnd
    suspend fun tryExceptionHandler() = coroutineScope {
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Exception $throwable caught")
        }

        val scope = CoroutineScope(SupervisorJob() + handler)

        scope.launch {
            delay(1000)
            throw Error("Some error")
        }

        scope.launch {
            delay(2000)
            println("Will be printed")
        }

        delay(3000)
    }
}