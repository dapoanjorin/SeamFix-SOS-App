package com.seamfix.sosapp.util

import kotlinx.coroutines.CoroutineDispatcher

interface AppCoroutineDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}