package com.example.core.domain.event

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<Ticket>>
}