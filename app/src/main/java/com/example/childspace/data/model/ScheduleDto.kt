package com.example.childspace.data.model

data class ScheduleDto(
    val id: String,
    val groupId: String,
    val teacherId: String?,
    val subjectId: String?,
    val groupName: String?,
    val teacherName: String?,
    val subjectName: String?,
    val roomName: String,
    val startTime: String,
    val endTime: String
)