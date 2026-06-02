package com.example.ticketapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.formatDate
import com.example.ticketapp.viewmodel.EventDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateToTickets: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) { viewModel.load(eventId) }

    LaunchedEffect(state.navigateToTickets) {
        if (state.navigateToTickets) {
            viewModel.consumeNavigation()
            onNavigateToTickets()
        }
    }

    // Ödeme onay diyaloğu
    if (state.showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPaymentDialog() },
            title = { Text("Ödeme Onayı") },
            text = {
                Text("Toplam ₺${state.totalCents / 100} tutarında ödeme yapılacak. Onaylıyor musunuz?")
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmPayment() }) {
                    Text("Onayla")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissPaymentDialog() }) {
                    Text("İptal")
                }
            }
        )
    }

    // Hata diyaloğu
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.consumeError() },
            title = { Text("Hata") },
            text = { Text(state.errorMessage!!) },
            confirmButton = {
                if (state.capacityExceeded) {
                    Button(onClick = {
                        viewModel.consumeError()
                        viewModel.load(eventId)
                    }) { Text("Yenile") }
                } else {
                    TextButton(onClick = { viewModel.consumeError() }) { Text("Tamam") }
                }
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }
            }
            state.event != null -> {
                val event = state.event!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(event.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(event.venue, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(formatDate(event.startsAt), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(event.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))

                    Text("Bilet Türleri", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    event.ticketTypes.forEach { tt ->
                        val max = minOf(20, tt.remaining.toInt())
                        val quantity = state.selections[tt.id] ?: 0

                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tt.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "${tt.remaining}/${tt.capacity} kalan",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "₺${tt.priceCents / 100}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(
                                onClick = { viewModel.decrement(tt.id) },
                                enabled = quantity > 0
                            ) {
                                Text("-", style = MaterialTheme.typography.titleLarge)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("$quantity", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.increment(tt.id, max) },
                                enabled = quantity < max
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    HorizontalDivider()
                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Toplam: ₺${state.totalCents / 100}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { viewModel.createPurchase() },
                            enabled = state.hasSelection && !state.isPurchasing && !state.isPaying
                        ) {
                            if (state.isPurchasing || state.isPaying) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Satın Al")
                            }
                        }
                    }
                }
            }
        }
    }
}