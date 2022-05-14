import kotlinx.coroutines.*

class ChildrenAwaiting {

    /**
    // (1 sec)
    // Test1
    // (1 sec)
    // Test2
    // All tests are done
     */
    fun waitChildren() = runBlocking {
        val job1 = launch {
            delay(1000)
            println("Test1")
        }
        val job2 = launch {
            delay(2000)
            println("Test2")
        }

        job1.join()
        job2.join()
        println("All tests are done")
    }

    fun waitChildrenWithCoroutineContext() = runBlocking {
        launch {
            delay(1000)
            println("Test1")
        }
        launch {
            delay(2000)
            println("Test2")
        }

        val children = coroutineContext.job.children
        val childrenNum = children.count()
        println("Number of children: $childrenNum")
        children.forEach { it.join() }
        println("All tests are done")
    }

    /**
     * A common mistake is to create a job using the Job() factory function, start some coroutines on it, and then use
     * join on the job. Such a program will never end, because Job is still in an active state, even when all its
     * children are finished. It is because this context is still ready to be used by other coroutines.
     */
    fun neverEnding() = runBlocking {
        val job = Job()
        launch(job) { // the new job replaces one from parent
            delay(1000)
            println("Text 1")
        }
        launch(job) { // the new job replaces one from parent
            delay(2000)
            println("Text 2")
        }
        job.join() // Here we will await forever
        println("Will not be printed")
    }

    fun ending() = runBlocking {
        val job = Job()
        launch(job) { // the new job replaces one from parent
            delay(1000)
            println("Text 1")
        }
        launch(job) { // the new job replaces one from parent
            delay(2000)
            println("Text 2")
        }
        job.children.forEach { it.join() }
    }

    /**
    // Rep0
    // Rep1
    // Rep2
    // Rep3
    // Rep4
    // Done
    Be aware: if you call job.complete() it will not stop the coroutine. If it has still some running operation
    it will be finished but new task not will be scheduled.
     */
    fun completeManually() = runBlocking {
        val job = Job()
        launch(job) {
            repeat(5) { num ->
                delay(200)
                println("Rep$num")
            }
        }
        launch {
            delay(500)
            job.complete()
        }
        job.join()
        launch(job) {
            println("Will not be printed")
        }
        println("Done")
    }

    /**
    // Rep0
    // Rep1
    // Done
     */
    fun completeManuallyWithException() = runBlocking {
        val job = Job()
        launch(job) {
            repeat(5) { num ->
                delay(200)
                println("Rep$num")
            }
        }
        launch {
            delay(500)
            job.completeExceptionally(Throwable("Some error"))
        }
        job.join()
        launch(job) {
            println("Will not be printed")
        }
        println("Done")
    }

    fun launchParentJob() = runBlocking {
        val parentJob = Job()
        val job = Job(parentJob)
        launch(job) {
            delay(1000)
            println("Text 1")
        }
        launch(job) {
            delay(2000)
            println("Text 2")
        }
        delay(1100)
        parentJob.cancel()
        job.children.forEach { it.join() }
    }
}