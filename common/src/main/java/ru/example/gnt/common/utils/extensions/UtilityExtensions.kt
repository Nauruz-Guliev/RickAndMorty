package ru.example.gnt.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.scan

fun String.cutLastOccurence(textToDrop: String): String {
    return if(this.endsWith(textToDrop)) {
        this.substring(0, this.length - textToDrop.length)
    } else {
        this
    }
}

fun <T> Flow<T>.flowWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = callbackFlow {
    lifecycle.repeatOnLifecycle(minActiveState) {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}

@ExperimentalCoroutinesApi
fun <T> Flow<T>.scan(count: Int): Flow<List<T?>> {
    val items = List<T?>(count) { null }
    return this.scan(items) { previous, value ->
        previous.drop(1) + value
    }
}
