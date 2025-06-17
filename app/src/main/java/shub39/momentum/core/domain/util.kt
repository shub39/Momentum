package shub39.momentum.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

fun <T, K> observePreferenceFlow(
    flow: Flow<T>,
    scope: CoroutineScope,
    state: MutableStateFlow<K>,
    update: (K, T) -> K
): Job {
    return flow
        .onEach { pref ->
            state.update { currentState ->
                update(currentState, pref)
            }
        }
        .launchIn(scope)
}