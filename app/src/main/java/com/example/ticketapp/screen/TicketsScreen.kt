package com.example.ticketapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.event.Ticket
import com.example.core.domain.event.TicketStatus
import com.example.ticketapp.viewmodel.TicketsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    onTicketClick: (String) -> Unit,
    viewModel: TicketsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "My tickets",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 25.dp)
        )

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.tickets.isEmpty() -> {
                    Text(
                        text = "Henüz biletin yok",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.tickets) { ticket ->
                            TicketCard(ticket = ticket,
                                onClick = { onTicketClick(ticket.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketCard(ticket: Ticket, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ticket.eventName.ifBlank { "Event" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (ticket.eventDate.isNotBlank()) {
                    Text(
                        text = ticket.eventDate,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                if (ticket.ticketTypeName.isNotBlank()) {
                    Text(
                        text = ticket.ticketTypeName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text(
                    text = "#${ticket.id.take(8)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            StatusBadge(status = ticket.status)
        }
    }
}

@Composable
private fun StatusBadge(status: TicketStatus) {
    val (text, color) = when (status) {
        TicketStatus.VALID -> "Geçerli" to Color(0xFF2E7D32)
        TicketStatus.USED -> "Kullanıldı" to Color(0xFF757575)
        TicketStatus.CANCELLED -> "İptal" to Color(0xFFC62828)
        TicketStatus.UNKNOWN -> "Bilinmiyor" to Color(0xFF757575)
    }
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyMedium
    )
}