package com.example.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.event.Event
import com.example.core.domain.event.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventsUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val errorMessage: String? = null
)

class EventsViewModel(
    private val eventRepository: EventRepository //Constructor injection.Koin AppModule'de tanıtılınca otomatik enjekte edilecek.
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow() //Dışarıya read-only versiyon. UI bu StateFlow'u dinler, değiştiremez.

    init {
        loadEvents() //ekran açıldığı an istek gider. kullanıcı bir şey yapmasa bile
    }

    fun loadEvents() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { events ->
                    _uiState.update {
                        it.copy(isLoading = false, events = events, errorMessage = null)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Etkinlikler yüklenemedi"
                        )
                    }
                }
        }
    }
}