package com.example.core.domain
data class Ticket(
    val id: String,
    val qrCode: String,
    val status: TicketStatus,
    val ticketTypeId: String
)

enum class TicketStatus {
    VALID,
    USED,
    CANCELLED,
    UNKNOWN;

    companion object {
        fun fromApi(value: String?): TicketStatus = when (value?.uppercase()) {
            "VALID" -> VALID
            "USED" -> USED
            "CANCELLED" -> CANCELLED
            else -> UNKNOWN
        }
    }
}