package com.example.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.event.Event
import com.example.core.domain.event.EventRepository
import com.example.core.domain.purchase.PurchaseItem
import com.example.core.domain.purchase.PurchaseRepository
import com.example.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val event: Event? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selections: Map<String, Int> = emptyMap(),
    // Purchase akışı
    val isPurchasing: Boolean = false,
    val showPaymentDialog: Boolean = false,
    val pendingPurchaseId: String? = null,
    val isPaying: Boolean = false,
    val navigateToTickets: Boolean = false,
    val capacityExceeded: Boolean = false
) {
    val totalCents: Long get() = event?.ticketTypes
        ?.sumOf { tt -> (selections[tt.id] ?: 0) * tt.priceCents } ?: 0L

    val hasSelection: Boolean get() = selections.values.any { it > 0 }
}

class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    fun load(id: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            eventRepository.getEvent(id)
                .onSuccess { event -> _state.update { it.copy(isLoading = false, event = event, capacityExceeded = false) } }
                .onFailure { error -> _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) } }
        }
    }

    fun increment(ticketTypeId: String, max: Int) {
        _state.update { state ->
            val current = state.selections[ticketTypeId] ?: 0
            if (current < max) state.copy(selections = state.selections + (ticketTypeId to current + 1))
            else state
        }
    }

    fun decrement(ticketTypeId: String) {
        _state.update { state ->
            val current = state.selections[ticketTypeId] ?: 0
            if (current > 0) state.copy(selections = state.selections + (ticketTypeId to current - 1))
            else state
        }
    }

    fun createPurchase() {
        val state = _state.value
        val items = state.selections
            .filter { it.value > 0 }
            .map { PurchaseItem(ticketTypeId = it.key, quantity = it.value) }

        if (items.isEmpty()) return

        _state.update { it.copy(isPurchasing = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseRepository.createPurchase(items)
                .onSuccess { purchase ->
                    _state.update {
                        it.copy(
                            isPurchasing = false,
                            showPaymentDialog = true,
                            pendingPurchaseId = purchase.id
                        )
                    }
                }
                .onFailure { error ->
                    val isCapacity = error.toUserMessage().contains("Stok yetersiz")
                    _state.update {
                        it.copy(
                            isPurchasing = false,
                            errorMessage = error.toUserMessage(),
                            capacityExceeded = isCapacity
                        )
                    }
                }
        }
    }

    fun confirmPayment() {
        val purchaseId = _state.value.pendingPurchaseId ?: return
        _state.update { it.copy(isPaying = true, showPaymentDialog = false) }

        viewModelScope.launch {
            purchaseRepository.pay(purchaseId)
                .onSuccess {
                    _state.update { it.copy(isPaying = false, navigateToTickets = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isPaying = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun dismissPaymentDialog() {
        _state.update { it.copy(showPaymentDialog = false, pendingPurchaseId = null) }
    }

    fun consumeNavigation() {
        _state.update { it.copy(navigateToTickets = false) }
    }

    fun consumeError() {
        _state.update { it.copy(errorMessage = null) }
    }
}