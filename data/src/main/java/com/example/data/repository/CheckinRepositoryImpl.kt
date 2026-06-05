package com.example.data.repository

import com.example.core.domain.checkin.CheckinRepository
import com.example.data.remote.CheckinApi
import com.example.data.util.runCatchingApi

class CheckinRepositoryImpl(
    private val checkinApi: CheckinApi
) : CheckinRepository {
    override suspend fun scan(qrCode: String): Result<Unit> =
        runCatchingApi { checkinApi.scan(qrCode) }.map { }
}