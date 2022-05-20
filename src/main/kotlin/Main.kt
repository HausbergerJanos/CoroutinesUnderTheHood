import kotlinx.coroutines.*

/**
 * Coroutine builders additionally cancel their parents as well, and each cancelled parent cancels all its children.
 */

suspend fun main() {
    //propagateException()
    //wrapLaunchWithTryCatch()
    //launchSupervisorJob()
    //launchAsCommonMistake()
    //launchAsBestPractise()
    //launchSupervisorScope()
    //launchSupervisorScopeAsync()

    val nonProp = NonPropagating()
    //nonProp.test()
    nonProp.tryExceptionHandler()
}

/**
 *  Let’s look at the example below: once a coroutine gets an exception, it cancels itself and propagates the exception
 *  to its parent (launch). The parent cancels itself, all its children, and propagates the exception to its parent
 *  (runBlocking). runBlocking is a root coroutine (has no parent), so it just ends the program (runBlocking rethrows
 *  the exception).
 *
 * // Will be printed
 * // Exception in thread "main" java.lang.Error: Some error...
 */
private fun propagateException() = runBlocking {
    launch {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will not be printed")
        }
        launch {
            delay(500) // faster than the exception
            println("Will be printed")
        }
    }

    launch {
        delay(2000)
        println("Will not be printed")
    }
}

/**
 * Catching the exception before it breaks a coroutine is helpful, but after that, it is too late. Communication
 * happens via a job, so wrapping a coroutine builder with try-catch is not helpful at all.
 */
private fun wrapLaunchWithTryCatch() = runBlocking {
    // Don't wrap in a try-catch here. It will be ignored.
    try {
        launch {
            delay(1000)
            throw Error("Some error")
        }
    } catch (e: Throwable) { // nope, does not help here
        println("Will not be printed")
    }
    launch {
        delay(2000)
        println("Will not be printed")
    }
}

// Exception...
// Will be printed
private fun launchSupervisorJob() = runBlocking {
    val scope = CoroutineScope(SupervisorJob())
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

private fun launchAsCommonMistake() = runBlocking {
    // Don't do that, SupervisorJob with one children
    // and no parent works similar to just Job
    launch(SupervisorJob()) { // 1
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will not be printed")

        }
    }
    delay(3000)
}

// (1 sec)
// Exception...
// (1 sec)
// Will be printed
private fun launchAsBestPractise() = runBlocking {
    val job = SupervisorJob()
    launch(job) {
        delay(1000)
        throw Error("Some error")
    }
    launch(job) {
        delay(2000)
        println("Will be printed")
    }
    job.join()
}

// Exception...
// Will be printed
// (1 sec)
// Done
private fun launchSupervisorScope() = runBlocking {
    supervisorScope {
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will be printed")
        }
    }
    delay(1000)
    println("Done")
}

// MyException
// Text2
private suspend fun launchSupervisorScopeAsync() = supervisorScope {
    val str1 = async<String> {
        delay(1000)
        throw Error("Some error")
    }
    val str2 = async {
        delay(2000)
        "Text2"
    }
    try {
        println(str1.await())
    } catch (e: Exception) {
        println(e)
    }
    println(str2.await())
}