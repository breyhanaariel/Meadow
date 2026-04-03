package com.meadow.core.network.interceptors

import dagger.Lazy
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

interface AccessTokenProvider {
    fun getAccessToken(): String?
}

class AuthInterceptor @Inject constructor(
    private val tokenProvider: Lazy<AccessTokenProvider>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider.get().getAccessToken()

        val request =
            if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }

        return chain.proceed(request)
    }
}
