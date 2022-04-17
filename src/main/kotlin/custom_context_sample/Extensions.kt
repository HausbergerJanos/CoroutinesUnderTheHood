package custom_context_sample

import kotlin.coroutines.coroutineContext

suspend fun makeUser(name: String) = User(
    id = nextUuid(),
    name = name
)

suspend fun nextUuid(): String =
    checkNotNull(coroutineContext[UuidProviderContext]) {
        "UuidProviderContext not present"
    }.nextUuid()