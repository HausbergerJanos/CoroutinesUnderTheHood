import kotlinx.coroutines.*

suspend fun main() {
    //baseCancellation()
    //cancellationWithoutJoin()
    //cancelJobFactory()
    //cancellationWithException()
    //testCancellingState()
    //nonCancellable()
    //invokeOnCompletion()
    //tryToStop()
    //stopWithYield()
    //stopWithStateCheck()
    stopWithEnsureIsActive()
}

private fun baseCancellation() = runBlocking {
    val job = launch {
        repeat(1_000) { i ->
            delay(200)
            println("Printing $i")
        }
    }

    delay(1100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
}

private fun cancellationWithoutJoin() = runBlocking {
    val job = launch {
        repeat(1_000) { i ->
            delay(100)
            Thread.sleep(100) // We simulate long operation
            println("Printing $i")
        }
    }

    delay(1000)
    job.cancel()
    println("Cancelled successfully")
}

private fun cancelJobFactory() = runBlocking {
    val job = Job()
    launch(job) {
        repeat(1_000) { i ->
            delay(200)
            println("Printing $i")
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}

private fun cancellationWithException() = runBlocking {
    val job = Job()
    launch(job) {
        try {
            repeat(1_000) { i ->
                delay(200)
                println("Printing $i")
            }
        } catch (e: CancellationException) {
            println(e)
            throw e
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
    delay(1000)
}

/**
 * The Job is already in a “Cancelling” state, where suspension or starting another coroutine is not possible at all.
 * If we try to start another coroutine, it will just be ignored. If we try to suspend, it will throw CancellationException.
 */
private fun testCancellingState() = runBlocking {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("Job is done")
        } finally {
            println("Finally")
            launch { // will be ignored
                println("Will not be printed")
            }
            delay(1000) // here exception is thrown
            println("Will not be printed")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancel done")
}

/**
 * Sometimes we truly need to use a suspending call when a coroutine is already cancelled. For instance,wemight need
 * to roll back changes on a database. Then the preferred way is to wrap this call with the withContext(NonCancellable) function.
 */
private fun nonCancellable() = runBlocking {
    val job = Job()
    launch(job) {
        try {
            delay(200)
            println("Coroutine finished")
        } finally {
            println("Finally")
            withContext(NonCancellable) {
                delay(1000L)
                println("Cleanup done")
            }
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}

private fun invokeOnCompletion() = runBlocking {
    val job = launch {
        delay(1000)
    }
    job.invokeOnCompletion { exception: Throwable? ->
        println("Finished")
    }
    delay(400)
    job.cancelAndJoin()
}

/**
 * There is no suspension point in the coroutine, so cancellation is not working
 */
private fun tryToStop() = runBlocking {
    val job = Job()
    launch(job) {
        repeat(1_00) { i ->
            Thread.sleep(200) // We might have some
            // complex operations or reading files here
            println("Printing $i")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancelled successfully")
    delay(1000)
}

/**
 * The yield function suspends and immediately resumes a coroutine. This gives space for whatever needs to happen during
 * suspension (or resuming), including cancellation (or changing thread using dispatcher).
 */
private fun stopWithYield() = runBlocking {
    val job = Job()
    launch(job) {
        repeat(1_00) { i ->
            Thread.sleep(200) // We might have some
            // complex operations or reading files here
            yield()
            println("Printing $i")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancelled successfully")
    delay(1000)
}

private suspend fun stopWithStateCheck() = coroutineScope {
    val job = Job()
    launch(job) {
        do {
            //Thread.sleep(200)
            println("Printing")
        } while (isActive)
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}

/**
 * Alternatively, we might use the ensureActive() function, that throws CancellationException if Job is not active.
 *
 * The result of ensureActive() and yield() seem similar, but they are very different. The function ensureActive() needs
 * to be called on a CoroutineScope (or CoroutineContext, or Job). All it does is throw an exception if the job is not
 * active anymore. It is lighter, and so should be generally preferred. The function yield is a regular top-level suspension
 * function. It does not need any scope, so it can be used in regular suspending functions. Since it does suspension and
 * resuming, other effects might happen, like thread changing if we use a dispatcher with a pool of threads
 */
private suspend fun stopWithEnsureIsActive() = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(1000) { num ->
            Thread.sleep(200)
            ensureActive()
            println("Printing $num")
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}