package custom_context_sample

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CreateUser {

    fun init() = runBlocking {
        productionCase()
        testCase()
    }

    private suspend fun productionCase() = withContext(RealUuidProviderContext()) {
        println(makeUser("Jani"))
    }

    private suspend fun testCase() = withContext(FakesUuidProviderContext("FAKE_UUID")) {
        println(makeUser("Jani"))
    }
}