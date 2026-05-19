package com.example.data.repository

import com.example.core.domain.AuthRepository
import com.example.core.domain.AuthSession
import com.example.core.domain.User
import com.example.core.domain.UserRole
import com.example.data.dto.CredentialsDto
import com.example.data.local.TokenStore
import com.example.data.remote.AuthApi
import com.example.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = tokenStore.accessToken.map { it != null }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email = email, password = password))
    }.onSuccess {
        // jwt'i bi yere yaz..
        tokenStore.save(it.accessToken, it.refreshToken)
    }
        .map {
                tokenPairDto -> AuthSession(
            user = User(
                tokenPairDto.user.id, tokenPairDto.user.email, UserRole.fromApi(tokenPairDto.user.role),
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken)
        }


    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.register(CredentialsDto(email = email, password = password)) //Retrofit'in oluşturduğu API client'a istek atıyo
    }.map { i ->
        AuthSession(
            user = User(
                i.user.id, i.user.email, UserRole.fromApi(i.user.role)
            ),
            accessToken = i.accessToken,
            refreshToken = i.refreshToken
        )
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }
}