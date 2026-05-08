package com.example.data.di

import com.example.core.domain.AuthRepository
import com.example.data.remote.AuthApi
import com.example.data.repository.AuthRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://tickets-api.halitkalayci.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}