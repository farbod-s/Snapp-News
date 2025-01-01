package com.snappbox.data.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

inline fun <T> mutableStateFlow(
    scope: CoroutineScope, initialValue: T, producer: (subscriptionCount: StateFlow<Int>) -> Flow<T>
): MutableStateFlow<T> {
    val state = MutableStateFlow(initialValue)
    producer(state.subscriptionCount).launchIn(scope, state)
    return state
}

fun <T> Flow<T>.launchIn(scope: CoroutineScope, collector: FlowCollector<T>): Job = scope.launch {
    collect(collector)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.flowWhileShared(
    subscriptionCount: StateFlow<Int>, started: SharingStarted
): Flow<T> {
    return started.command(subscriptionCount).distinctUntilChanged().flatMapLatest {
        when (it) {
            SharingCommand.START -> this
            SharingCommand.STOP,
            SharingCommand.STOP_AND_RESET_REPLAY_CACHE -> emptyFlow()
        }
    }
}