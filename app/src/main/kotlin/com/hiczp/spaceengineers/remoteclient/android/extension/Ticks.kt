package com.hiczp.spaceengineers.remoteclient.android.extension

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

typealias Ticks = Long

private val offset = Duration.between(
    Instant.parse("0001-01-01T00:00:00.00Z"),
    Instant.ofEpochSecond(0)
).seconds
private val zoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().rawOffset / 1000)

fun Ticks.toLocalDateTime() =
    LocalDateTime.ofEpochSecond(
        this / 10_000_000 - offset,
        0,
        zoneOffset
    )!!
