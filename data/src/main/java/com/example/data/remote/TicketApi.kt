package com.example.data.remote

import com.example.data.dto.TicketDto
import retrofit2.http.GET


interface TicketApi {
    @GET("me/tickets")
    suspend fun getMyTickets(): List<TicketDto>
}