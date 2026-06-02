package com.example.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.event.Ticket
import com.example.core.domain.event.TicketRepository
import com.example.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TicketDetailViewModel(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailUiState())
    val state: StateFlow<TicketDetailUiState> = _state.asStateFlow()

    fun load(id: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            ticketRepository.getTicket(id)
                .onSuccess { ticket ->
                    _state.update { it.copy(isLoading = false, ticket = ticket) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }
}