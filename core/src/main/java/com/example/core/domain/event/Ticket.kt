package com.example.core.domain.event
data class Ticket(
    val id: String,
    val qrCode: String,
    val status: TicketStatus,
    val ticketTypeId: String,
    val eventName: String = "",
    val eventDate: String = "",
    val ticketTypeName: String = ""
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