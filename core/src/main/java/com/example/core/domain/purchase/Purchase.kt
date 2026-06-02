package com.example.core.domain.purchase

data class Purchase(
    val id: String,
    val status: PurchaseStatus,
    val items: List<PurchaseItem>,
    val totalCents: Long
)

data class PurchaseItem(
    val ticketTypeId: String,
    val quantity: Int
)

enum class PurchaseStatus { PENDING, PAID }

data class Ticket(
    val id: String,
    val qrCode: String,
    val status: TicketStatus
)

enum class TicketStatus { VALID, USED, CANCELLED }