package custom_context_sample

import java.util.*

class RealUuidProviderContext : UuidProviderContext() {
    override fun nextUuid(): String {
        return UUID.randomUUID().toString()
    }
}