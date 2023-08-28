package com.github.terrakok.modo.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.terrakok.modo.Screen
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.plus
import java.util.UUID

public val ScreenModel.coroutineScope: CoroutineScope
    get() = ScreenModelStore.getOrPutDependency(
        screenModel = this,
        name = "ScreenModelCoroutineScope",
        factory = { key -> MainScope() + CoroutineName(key) },
        onDispose = { scope -> scope.cancel() }
    )

@Composable
public inline fun <reified T : ScreenModel> Screen.rememberScreenModel(
    tag: String? = null,
    crossinline factory: @DisallowComposableCalls () -> T
): T =
    remember(ScreenModelStore.getKey<T>(this, tag)) {
        ScreenModelStore.getOrPut(this, tag, factory)
    }

@PublishedApi
internal const val ON_SCREEN_REMOVED_CALLBACK_NAME = "OnScreenRemovedCallBack"

@Composable
public inline fun Screen.OnScreenRemoved(
    tag: String = rememberSaveable { UUID.randomUUID().toString() },
    crossinline onScreenRemoved: @DisallowComposableCalls () -> Unit
): Unit {
    LaunchedEffect(tag) {
        ScreenModelStore.getOrPutDependency(
            screen = this@OnScreenRemoved,
            name = ON_SCREEN_REMOVED_CALLBACK_NAME,
            tag = tag,
            onDispose = { onScreenRemoved() },
            factory = { Any() }
        )
    }
}

public interface ScreenModel {

    public fun onDispose() {}
}

public abstract class StateScreenModel<S>(initialState: S) : ScreenModel {

    protected val mutableState: MutableStateFlow<S> = MutableStateFlow(initialState)
    public val state: StateFlow<S> = mutableState.asStateFlow()
}
