package com.github.terrakok.modo.android.compose

import java.util.concurrent.atomic.AtomicInteger

internal val screenCounterKey = AtomicInteger(-1)

fun generateScreenKey(): String = "Screen#${screenCounterKey.incrementAndGet()}"

fun restoreScreenCounter(value: Int) {
//    if (screenCounterKey.get() < 0) {
//        screenCounterKey.set(valueProvider())
//    }
    require(screenCounterKey.get() == -1 || screenCounterKey.get() == value) {
        "Trying to restore screen count, when screen count is $screenCounterKey != 0. " +
                "You must only call restoreScreenKey when screenCount == 0."
    }
    screenCounterKey.set(value)
}