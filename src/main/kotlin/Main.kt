import custom_context_sample.CreateUser
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

fun main() {
    //fold()
    //subtracting()
    //replace()
    //add()
    //find()

    //testBuilderWithContext()
    testCustomContext()
}

private fun testCustomContext() {
    val createUser = CreateUser()
    createUser.init()
}

private fun testBuilderWithContext() {
    val contextAndBuilders = ContextAndBuilders()
    contextAndBuilders.logSomething()
    println("/////////////////////////////////")
    contextAndBuilders.logSomethingAndOverrideChildContext()
    println("/////////////////////////////////")
    contextAndBuilders.contextInSuspendingFunction()
    println("/////////////////////////////////")
    contextAndBuilders.tryCustomContext()
}

/**
 * Adding contexts
 */
private fun add() {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name) // Name1
    println(ctx1[Job]?.isActive) // null

    val ctx2: CoroutineContext = Job()
    println(ctx2[CoroutineName]?.name) // null
    println(ctx2[Job]?.isActive) // true, because "Active"
    // is the default state of a job created this way

    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name) // Name1
    println(ctx3[Job]?.isActive) // true
}

/**
 * When another element with the same key is added, just like in a map,
 * the new element replaces the previous one.
 */
private fun replace() {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name) // Name1

    val ctx2: CoroutineContext = CoroutineName("Name2")
    println(ctx2[CoroutineName]?.name) // Name2

    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name) // Name2

}

/**
 * Finding elements in CoroutineContext
 */
private fun find() {
    val ctx: CoroutineContext = CoroutineName("A name")
    val coroutineName: CoroutineName? = ctx[CoroutineName]
    // or ctx.get(CoroutineName)
    println(coroutineName?.name) // A name
    val job: Job? = ctx[Job] // or ctx.get(Job)
    println(job) // null
}

/**
 * Subtracting elements in CoroutineContext
 */
private fun subtracting() {
    val ctx = CoroutineName("Name1") + Job()
    println(ctx[CoroutineName]?.name) // Name1
    println(ctx[Job]?.isActive) // true

    val ctx2 = ctx.minusKey(CoroutineName)
    println(ctx2[CoroutineName]?.name) // null
    println(ctx2[Job]?.isActive) // true

    val ctx3 = (ctx + CoroutineName("Name2"))
        .minusKey(CoroutineName)
    println(ctx3[CoroutineName]?.name) // null
    println(ctx3[Job]?.isActive) // true
}

private fun fold() {
    val ctx = CoroutineName("Name1") + Job()

    ctx.fold("start") { acc, element -> "$acc $element " }
        .also(::println)

    val empty = emptyList<CoroutineContext>()
    ctx.fold(empty) { acc, element -> acc + element }
        .joinToString()
        .also(::println)
}