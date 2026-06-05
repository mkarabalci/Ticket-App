package com.example.core.domain.checkin

interface CheckinRepository {
    suspend fun scan(qrCode: String): Result<Unit>
}