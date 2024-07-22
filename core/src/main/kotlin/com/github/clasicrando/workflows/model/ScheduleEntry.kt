package com.github.clasicrando.workflows.model

import kotlinx.datetime.LocalTime

data class ScheduleEntry(
    val dayOfWeek: Short,
    val timeOfDay: LocalTime,
)
