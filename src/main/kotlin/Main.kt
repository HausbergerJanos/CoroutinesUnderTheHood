import kotlinx.coroutines.*

/**
There are three essential coroutine builders provided by kotlinx.coroutines library:
- launch
- runBlocking
- async
 */

/**
fun main() {
    //launchSingleAsyncBuilder()
    launchMultipleAsyncBuilder()
}
*/

fun main() = runBlocking {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    delay(2000L)
}

/**
fun main() {
    //launchGlobalScopes()
    launchRunBlocking()
}
*/

/**
 * The way how launch works is conceptually similar to starting a new
 * thread (thread function). We just start a coroutine, and it will run independently.
 * -----------------------------------------------------------------------------------
 *  Hello,
 *  (1 sec)
 *  World!
 *  World!
 *  World!
 *  ----------------------------------------------------------------------------------
 */
fun launchGlobalScopes() {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    Thread.sleep(2000L)
}

/**
 * runBlocking is a very atypical builder. It blocks the thread it has been started on, whenever its coroutine is
 * suspended (similar to suspending main). This means that delay(1000L) inside runBlocking will behave like a
 * Thread.sleep(1000L).
 * -----------------------------------------------------------------------------------
 *  (1 sec)
 *  World!
 *  (1 sec)
 *  World!
 *  (1 sec)
 *  World!
 *  Hello,
 *  ----------------------------------------------------------------------------------
 *  There are actually a couple of specific use cases for runBlocking where it’s used. The first one is the main function,
 *  where we need to block the thread, because otherwise the program will end. Another common use case is unit tests,
 *  where we need to block the thread for the same reason.
 */
fun launchRunBlocking() {
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}

/**
 * Just like the launch builder, async starts a coroutine immediately when it is called. So it is away to start a few
 * processes at once and then await their results together.
 */
private fun launchSingleAsyncBuilder() = runBlocking {
    val resultDeferred: Deferred<Int> = GlobalScope.async {
        delay(1000)
        42
    }

    val result = resultDeferred.await()
    println(result)
}

/**
 * The returned Deferred stores a value inside once it is produced, so once it is ready, it will be immediately returned
 * from await. Although if we call await before the value is produced, we are suspended until the value is ready.
 * -----------------------------------------------------------------------------
 * (1 sec)
 * Text 1
 * (2 sec)
 * Text 2
 * Text 3
 * -----------------------------------------------------------------------------
 */
private fun launchMultipleAsyncBuilder() = runBlocking {
    val res1 = GlobalScope.async {
        delay(1000L)
        "Text 1"
    }
    val res2 = GlobalScope.async {
        delay(3000L)
        "Text 2"
    }
    val res3 = GlobalScope.async {
        delay(2000L)
        "Text 3"
    }
    println(res1.await())
    println(res2.await())
    println(res3.await())

}
















