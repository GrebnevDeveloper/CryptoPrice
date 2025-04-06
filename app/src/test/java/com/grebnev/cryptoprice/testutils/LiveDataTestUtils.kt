package com.grebnev.cryptoprice.testutils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeForTesting(block: () -> Unit = {}): LiveDataValueCapture<T> {
    val capture = LiveDataValueCapture<T>()
    val observer = capture.toObserver()
    observeForever(observer)
    try {
        block()
    } finally {
        removeObserver(observer)
    }
    return capture
}

class LiveDataValueCapture<T> {
    private val _values = mutableListOf<T>()
    val values: List<T> get() = _values

    fun toObserver(): Observer<T> = Observer { _values.add(it) }
}