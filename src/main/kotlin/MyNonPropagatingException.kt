import kotlinx.coroutines.CancellationException

object MyNonPropagatingException : CancellationException()