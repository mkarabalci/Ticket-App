package com.example.data.remote

import retrofit2.http.POST
import retrofit2.http.Query

interface CheckinApi {
    @POST("checkin/scan")
    suspend fun scan(@Query("qrCode") qrCode: String)
}