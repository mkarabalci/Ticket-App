package com.example.ticketapp.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.auth.AuthRepository
import com.example.ticketapp.viewmodel.CheckinViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinScreen(
    viewModel: CheckinViewModel = koinViewModel(),
    authRepository: AuthRepository = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val contents = result.contents
        if (contents != null) {
            viewModel.scan(contents)
        }
    }

    fun startCameraScan() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("QR Kodu Çerçeveye Getir")
            setBeepEnabled(true)
            setOrientationLocked(false)
            setBarcodeImageEnabled(false)
        }
        scanLauncher.launch(options)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCameraScan()
    }

    fun onScanClick() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) startCameraScan()
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val width = bitmap.width
                val height = bitmap.height
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

                val source = com.google.zxing.RGBLuminanceSource(width, height, pixels)
                val binaryBitmap = com.google.zxing.BinaryBitmap(com.google.zxing.common.HybridBinarizer(source))
                val result = com.google.zxing.MultiFormatReader().decode(binaryBitmap)

                viewModel.scan(result.text)
            } catch (e: Exception) {
            }
        }
    }

    fun onGalleryClick() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }


    if (state.successMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.reset() },
            title = { Text("Sonuç") },
            text = {
                Text(
                    text = state.successMessage!!,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.reset() }) {
                    Text("Yeni Tarama")
                }
            }
        )
    }

    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.reset() },
            title = { Text("Hata") },
            text = { Text(state.errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.reset() }) {
                    Text("Tamam")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Görevli Ekranı — QR Check-in") },
                actions = {
                    IconButton(onClick = {
                        scope.launch { authRepository.logout() }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Çıkış"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bilet taramak için butona bas",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onScanClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("QR Kodu Tara")
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onGalleryClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("QR Kodu Tara - Galeri")
            }
        }
    }
}