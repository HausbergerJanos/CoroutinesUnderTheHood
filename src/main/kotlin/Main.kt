import kotlinx.coroutines.delay

/**
Let's see the following methods:

- suspend fun getUser(): User?
- suspend fun setUser(user: User)
- suspend fun checkAvailability(flight: Flight): Boolean

Under the hood they will be:

- fun getUser(continuation: Continuation<*>): Any?
- fun setUser(user: User, continuation: Continuation<*>): Any
- fun checkAvailability(
    flight: Flight,
    continuation: Continuation<*>
): Any

The result type under the hood is different from the originally declared. It changed from Unit and Boolean to Any,
and from User? to Any?. The reason is that a suspending function might be suspended, and so it might not return a
declared type. In such a case, it returns a special marker **COROUTINE_SUSPENDED**. Since getUser might return User? or
COROUTINE_SUSPENDED (that is of type Any), its result type must be the closest supertype of User? and Any, so it is Any?.
*/

suspend fun main() {
    myFunction()
}

/**
A very simple function that prints something before and after delay.

A simplified picture of how myFunction looks under the hood.

The function could be started from two places: either from the beginning (in case of a first call) or from the point
after suspension (in case of resuming from continuation).

fun myFunction(continuation: Continuation<Unit>): Any {
    // This function needs its own continuation to remember its state. At the beginning of its body, myFunction will
    // wrap the continuation (the parameter) with its own continuation (MyFunctionContinuation). This should be done
    // only if the continuation isn’t wrapped already. If it is, this is part of the resume process, and we should
    // keep the continuation unchanged
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    // To identify the current state, we use a field called label. At the start, it is 0, so the function will start
    // from the beginning.
    if (continuation.label == 0) {
        println("Before")
        // Before each suspension point, it is set to the next state, so that after resume we start from just
        // after suspension.
        continuation.label = 1
        // When delay is suspended, it returns COROUTINE_SUSPENDED, then myFunction returns COROUTINE_SUSPENDED,
        // the same is done by the function that called it, and the function that called this function, and all other
        // functions until the top of the call stack
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }

    if (continuation.label == 1) {
        println("After")
        return Unit
    }

    error("Impossible")
} */
suspend fun myFunction() {
    println("Before")
    delay(1000)
    println("After")
}