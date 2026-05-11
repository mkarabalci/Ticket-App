package com.example.data.repository

import com.example.core.domain.AuthRepository
import com.example.core.domain.AuthSession
import com.example.core.domain.User
import com.example.core.domain.UserRole
import com.example.data.dto.CredentialsDto
import com.example.data.remote.AuthApi
import com.example.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean>
        get() = TODO("Not yet implemented")

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email = email, password = password))
    }.onSuccess {
        // jwt'i bi yere yaz..
    }
        .map {
                tokenPairDto -> AuthSession(
            user = User(
                tokenPairDto.user.id, tokenPairDto.user.email, UserRole.fromApi(tokenPairDto.user.role),
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken)
        }
    /// backend -> (tokenPairDto) accesToken
    /// backend -> (tokenPairDto) jet

    /// backend -> (tokenPairDto) accessToken -> (AuthSession) accessToken -> tüm uygulama
    /// backend -> (tokenPairDto) jet -> (AuthSession) accessToken -> tüm uygulama


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