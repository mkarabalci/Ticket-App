package com.example.data.repository

import com.example.core.domain.event.Ticket
import com.example.core.domain.event.TicketRepository
import com.example.core.domain.event.TicketStatus
import com.example.data.dto.TicketDto
import com.example.data.remote.TicketApi
import com.example.data.util.runCatchingApi

class TicketRepositoryImpl(
    private val ticketApi: TicketApi
) : TicketRepository {

    override suspend fun getMyTickets(): Result<List<Ticket>> = runCatchingApi {
        ticketApi.getMyTickets().map { it.toDomain() }
    }
}

private fun TicketDto.toDomain() = Ticket(
    id = id,
    qrCode = qrCode,
    status = TicketStatus.fromApi(status),
    ticketTypeId = ticketTypeId
)