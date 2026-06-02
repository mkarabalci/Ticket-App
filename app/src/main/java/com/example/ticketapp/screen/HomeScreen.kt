package com.example.ticketapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.domain.auth.AuthRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEventsClick: () -> Unit,
    onTicketsClick: () -> Unit,
    authRepository: AuthRepository = koinInject()
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TicketAPP", fontWeight = FontWeight.Bold) },
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            onClick = onEventsClick,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Events",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            onClick = onTicketsClick,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tickets",
                    style = MaterialTheme.typography.headlineSmall
                )
               }
           }
       }
    }
}