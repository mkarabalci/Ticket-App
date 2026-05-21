package com.example.core.domain

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<Ticket>>
}