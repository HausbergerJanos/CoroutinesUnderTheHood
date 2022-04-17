import kotlin.coroutines.CoroutineContext

class CounterContext(
    private val name: String
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key
    private var nextNumber: Int = 0

    fun printNext() {
        println("$name: $nextNumber")
        nextNumber++
    }

    companion object Key: CoroutineContext.Key<CounterContext>
}