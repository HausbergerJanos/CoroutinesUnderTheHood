import kotlinx.coroutines.*

fun main() {
    val ca = ChildrenAwaiting()
    ca.waitChildren()
    //ca.waitChildrenWithCoroutineContext()
    //ca.neverEnding()
    //ca.ending()
    //ca.completeManually()
    //ca.completeManuallyWithException()
    //ca.launchParentJob()
}

/**
 * In the above example, the parent does not wait for its children, because it has no relation with them.
 * It is because the child uses job from the argument as a parent, so it has no relation to the runBlocking.
 */
/**
fun main(): Unit = runBlocking {
    launch(Job()) { // the new job replaces one from parent
        delay(1000)
        println("Will not be printed")
    }
}
// (prints nothing, finishes immediately)
*/

/**
fun main(): Unit = runBlocking {
    val job: Job = launch {
        delay(1000)
    }

    val parentJob: Job = coroutineContext.job
    // or coroutineContext[Job]!!
    println(job == parentJob) // false
    val parentChildren: Sequence<Job> = parentJob.children
    println(parentChildren.first() == job) // true
}
*/

/**
fun main(): Unit = runBlocking {
    val name = CoroutineName("Some name")
    val job = Job()

    launch(name + job) {
        val childName = coroutineContext[CoroutineName]
        println(childName == name) // true
        val childJob = coroutineContext[Job]
        println(childJob == job) // false
        println(childJob == job.children.first()) // true
    }
}
*/

/**
suspend fun main() = coroutineScope {
    // Job created with a builder is active
    val job = Job()
    println(job) // JobImpl{Active}@ADD
    // until we complete it with a method
    job.complete()
    println(job) // JobImpl{Completed}@ADD

    // launch is initially active by default
    val activeJob = launch {
        delay(1000)
        println("Active job finished")
    }
    println(activeJob) // StandaloneCoroutine{Active}@ADD
    // here we wait until this job is done
    activeJob.join() // (1 sec)
    println(activeJob) // StandaloneCoroutine{Completed}@ADD

    // launch started lazily is in New state
    val lazyJob = launch(start = CoroutineStart.LAZY) {
        delay(1000)
        println("Lazy job finished")
    }
    println(lazyJob) // LazyStandaloneCoroutine{New}@ADD
    // we need to start it, to make it active
    lazyJob.start()
    println(lazyJob) // LazyStandaloneCoroutine{New}@ADD
    lazyJob.join() // (1 sec)
    println(lazyJob) //LazyStandaloneCoroutine{Completed}@ADD
}*/