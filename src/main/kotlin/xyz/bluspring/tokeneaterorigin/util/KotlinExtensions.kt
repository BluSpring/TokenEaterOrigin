package xyz.bluspring.tokeneaterorigin.util

import kotlin.time.Duration

public inline val Duration.ticks: Int get() = (this.inWholeMilliseconds / 50).toInt()