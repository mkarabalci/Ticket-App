package com.example.data.dto.purchase

import kotlinx.serialization.Serializable

@Serializable
data class CreatePurchaseRequestDto(
    val items: List<PurchaseItemRequestDto>
)

@Serializable
data class PurchaseItemRequestDto(
    val ticketTypeId: String,
    val quantity: Int
)

@Serializable
data class PurchaseDto(
    val id: String,
    val status: String,
    val items: List<PurchaseItemDto> = emptyList(),
    val totalCents: Long = 0
)

@Serializable
data class PurchaseItemDto(
    val ticketTypeId: String,
    val quantity: Int
)

@Serializable
data class TicketDto(
    val id: String,
    val qrCode: String,
    val status: String
)