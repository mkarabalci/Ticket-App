package com.example.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.checkin.CheckinRepository
import com.example.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckinUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class CheckinViewModel(
    private val checkinRepository: CheckinRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckinUiState())
    val state: StateFlow<CheckinUiState> = _state.asStateFlow()

    fun scan(qrCode: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, successMessage = null, errorMessage = null) }
        viewModelScope.launch {
            checkinRepository.scan(qrCode)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, successMessage = "Bilet geçerli ✅") }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun reset() {
        _state.update { CheckinUiState() }
    }
}