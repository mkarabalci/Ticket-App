package com.example.data.remote

import com.example.data.dto.TicketDto
import retrofit2.http.GET
import retrofit2.http.Path


interface TicketApi {
    @GET("me/tickets")
    suspend fun getMyTickets(): List<TicketDto>

    @GET("me/tickets/{id}")
    suspend fun getTicket(@Path("id") id: String): TicketDto
}