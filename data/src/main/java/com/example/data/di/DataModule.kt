package com.example.data.di

import com.example.core.domain.auth.AuthRepository
import com.example.core.domain.event.EventRepository
import com.example.core.domain.event.TicketRepository
import com.example.core.domain.purchase.PurchaseRepository
import com.example.data.local.TokenStore
import com.example.data.network.AuthInterceptor
import com.example.data.network.TokenAuthenticator
import com.example.data.remote.AuthApi
import com.example.data.remote.EventApi
import com.example.data.remote.PurchaseApi
import com.example.data.remote.TicketApi
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.EventRepositoryImpl
import com.example.data.repository.TicketRepositoryImpl
import com.example.data.repository.PurchaseRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlin.jvm.java

private const val BASE_URL = "https://tickets-api.halitkalayci.com/"

//Named Dependencyler
private val REFRESH_CLIENT = named("refresh_client")
private val REFRESH_RETROFIT = named("refresh_retrofit")
private val REFRESH_API = named("refresh_api")


//Prjede ihityaç duyulan her dependecy için (data katmanı özelinde) tanımlama burada yapılır
val dataModule = module {

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

    single {
        TokenStore(context = get())
    }

    single { AuthInterceptor(tokenStore = get()) }

    single {
        TokenAuthenticator(
            tokenStore = get(),
            refreshApiProvider = { get(REFRESH_API) }
        )
    }

    // Refresh Stack
    single(REFRESH_CLIENT) {
        OkHttpClient.Builder().addInterceptor(get<HttpLoggingInterceptor>()).build()
    }

    single(REFRESH_RETROFIT)
    {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(REFRESH_CLIENT))
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single(REFRESH_API)
    {
        get<Retrofit>(REFRESH_RETROFIT).create(AuthApi::class.java)
    }
    // Refresh Stack


    // HTTP isteklerini yönetmek..
    single {
        OkHttpClient.Builder() //burdaki sıra önemlidir
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<TokenAuthenticator>())
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

    single {
        get<Retrofit>().create(AuthApi::class.java)
    }


    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            tokenStore = get()
        )
    }

    single {
        get<Retrofit>().create(EventApi::class.java)
    }

    single<EventRepository> {
        EventRepositoryImpl(
            eventApi = get()
        )
    }

    single {
        get<Retrofit>().create(TicketApi::class.java)
    }

    single<TicketRepository> {
        TicketRepositoryImpl(
            ticketApi = get()
        )
    }

    single {
        get<Retrofit>().create(PurchaseApi::class.java)
    }

    single<PurchaseRepository> {
        PurchaseRepositoryImpl(purchaseApi = get())
    }
}




//Scope(kapsam)
// 3 temel seçenek

// yaşam döngüsündeki bağımlılığın davranış biçimi

// Single (singleton) -> uygulama yaşam döngüsü boyunca tek örnek

// factory -> her çağırıldığı noktada yeni instance üretir. her fonksiyon içine bir örnek

//scoped -> Class -> tüm fonksiyonlarına 1 örnek