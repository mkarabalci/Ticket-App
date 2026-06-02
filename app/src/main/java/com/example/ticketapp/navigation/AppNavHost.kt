package com.example.ticketapp.navigation



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.core.domain.auth.AuthRepository
import com.example.ticketapp.screen.EventDetailScreen
import com.example.ticketapp.screen.EventsScreen
import com.example.ticketapp.screen.HomeScreen
import com.example.ticketapp.screen.LoginScreen
import com.example.ticketapp.screen.RegisterScreen
import com.example.ticketapp.screen.TicketDetailScreen
import com.example.ticketapp.screen.TicketsScreen
import org.koin.compose.koinInject


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
)
{
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)

    when(isLoggedIn)
    {
        null -> SplashScreen()
        true -> AuthedNavHost(navController)
        false -> UnAuthedNavHost(navController)
    }
}

@Composable
private fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Home){
        composable<Home> {
            HomeScreen(
                onEventsClick = { navController.navigate(Events) },
                onTicketsClick = { navController.navigate(Tickets) }
            )
        }
        composable<Events> {
            EventsScreen(onEventClick = { eventId -> navController.navigate(EventDetail(eventId)) })
        }
        composable<Tickets> {
            TicketsScreen(onTicketClick = { ticketId -> navController.navigate(TicketDetail(ticketId)) })
        }
        composable<EventDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<EventDetail>()
            EventDetailScreen(
                eventId = route.id,
                onNavigateToTickets = {
                    navController.navigate(Tickets) {
                        popUpTo(Events) { inclusive = false }
                    }
                }
            )
        }
        composable<TicketDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TicketDetail>()
            TicketDetailScreen(ticketId = route.id)
        }
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController){
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = { navController.navigate(Register) }
            )
        }
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {},
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
    }
}

