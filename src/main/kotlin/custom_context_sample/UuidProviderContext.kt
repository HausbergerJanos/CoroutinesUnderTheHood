package custom_context_sample

import kotlin.coroutines.CoroutineContext

abstract class UuidProviderContext : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    abstract fun nextUuid(): String

    companion object Key : CoroutineContext.Key<UuidProviderContext>
}