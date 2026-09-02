package br.com.mykytadu.core.network

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual fun isHttpLoggingEnabled(): Boolean = Platform.isDebugBinary
