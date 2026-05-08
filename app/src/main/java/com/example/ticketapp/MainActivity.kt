package com.example.ticketapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.core.domain.AuthRepository
import com.example.core.ui.theme.TicketAppTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicketAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var statusMessage by remember { mutableStateOf("") }

                    LoginScreen(
                        statusMessage = statusMessage,
                        onLoginClick = { email, password ->
                            statusMessage = "İstek atılıyor..."
                            lifecycleScope.launch {
                                Log.d("LoginTest", "İstek atılıyor...")
                                val result = authRepository.login(email, password)
                                result.fold(
                                    onSuccess = { session ->
                                        Log.d("LoginTest", "Başarılı: ${session.user.email}")
                                        statusMessage = "Başarılı: ${session.user.email}"
                                    },
                                    onFailure = { error ->
                                        Log.e("LoginTest", "Hata: ${error.message}", error)
                                        statusMessage = "Hata: ${error.message}"
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    statusMessage: String,
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("mehmet@example.com") }
    var password by remember { mutableStateOf("mehmet46") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Giriş Yap", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap")
        }
        if (statusMessage.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(statusMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

