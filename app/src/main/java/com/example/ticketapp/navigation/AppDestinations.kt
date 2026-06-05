package com.example.ticketapp.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login
@Serializable
object Register
@Serializable
object Home

@Serializable
object Events

@Serializable
object Tickets

@Serializable
data class EventDetail(val id: String)


@Serializable
data class TicketDetail(val id: String)

@Serializable object Checkin