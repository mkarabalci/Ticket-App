package com.example.data.di

import com.example.core.domain.AuthRepository
import com.example.data.remote.AuthApi
import com.example.data.repository.AuthRepositoryImpl
import kotlinx.serialization.json.Json

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://tickets-api.halitkalayci.com/"

val dataModule = module {
    // Scope (Kapsam)
    // 3 temel seçenek

    // Yaşam döngüsündeki bağımlılığın davranış biçimi

    // Single (Singleton) -> Uygulama yaşam döngüsü boyunca tek örnek.
    single {
        Json {
            ignoreUnknownKeys = true // Cevapta var olan ama classta olmayan alanları ignore et.
            explicitNulls = false
            isLenient = true
        }
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // HTTP isteklerini yönetmek..
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single { get<Retrofit>().create(AuthApi::class.java) }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get()
        )
    }
}




//Scope(kapsam)
// 3 temel seçenek

// yaşam döngüsündeki bağımlılığın davranış biçimi

// Single (singleton) -> uygulama yaşam döngüsü boyunca tek örnek

// factory -> her çağırıldığı noktada yeni instance üretir. her fonksiyon içine bir örnek

//scoped -> Class -> tüm fonksiyonlarına 1 örnek