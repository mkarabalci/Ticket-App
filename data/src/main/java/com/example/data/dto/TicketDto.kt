package com.example.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class TicketDto(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String,
    val ticketType: TicketTypeInfoDto? = null
)
@Serializable
data class TicketTypeInfoDto(
    val id: String,
    val name: String,
    val event: TicketEventInfoDto? = null
)

@Serializable
data class TicketEventInfoDto(
    val id: String,
    val name: String,
    val startsAt: String? = null,
    val place: String? = null
)