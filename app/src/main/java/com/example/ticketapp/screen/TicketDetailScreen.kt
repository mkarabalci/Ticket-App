package com.example.ticketapp.screen

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.example.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(ticketId) { viewModel.load(ticketId) }

    // Ekran parlaklığını maksimuma çek, ekrandan çıkınca geri al
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: -1f
        window?.attributes = window?.attributes?.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        onDispose {
            window?.attributes = window?.attributes?.apply {
                screenBrightness = originalBrightness
            }
        }
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
            state.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
            state.ticket != null -> {
                val ticket = state.ticket!!
                val qrBitmap = remember(ticket.qrCode) {
                    generateQrBitmap(ticket.qrCode)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = ticket.eventName.ifBlank { "Etkinlik" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (ticket.eventDate.isNotBlank()) {
                        Text(
                            text = ticket.eventDate,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = ticket.ticketTypeName.ifBlank { "Bilet" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(32.dp))

                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Kod",
                            modifier = Modifier.size(260.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "#${ticket.id.take(8)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}