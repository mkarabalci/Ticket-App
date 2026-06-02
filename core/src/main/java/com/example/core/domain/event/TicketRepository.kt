package com.example.core.domain.event

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<Ticket>>
    suspend fun getTicket(id: String): Result<Ticket>
}