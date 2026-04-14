package com.example.childspace.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childspace.data.model.ScheduleDto
import com.example.childspace.data.repository.ScheduleRepository
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    private val isTeacher: Boolean
) : ViewModel() {

    var schedule by mutableStateOf<List<ScheduleDto>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadSchedule() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = if (isTeacher) {
                repository.getTeacherSchedule()
            } else {
                repository.getChildrenSchedule()
            }

            if (result.isSuccess) {
                schedule = result.getOrDefault(emptyList())
            } else {
                errorMessage = result.exceptionOrNull()?.message
            }
            isLoading = false
        }
    }
}