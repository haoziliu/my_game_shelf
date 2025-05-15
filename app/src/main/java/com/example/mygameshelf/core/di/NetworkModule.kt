package com.example.mygameshelf.core.di

import com.example.mygameshelf.core.network.AuthInterceptor
import com.example.mygameshelf.data.remote.api.AuthApi
import com.example.mygameshelf.data.remote.api.GameApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://id.twitch.tv/oauth2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    @Named("ApiRetrofit")
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Client-ID", "BuildConfig.TWITCH_CLIENT_ID")
                            .build()
                        chain.proceed(request)
                    })
                    .build()
            )
            .build()
    }


    @Provides
    @Singleton
    fun provideAuthApi(@Named("AuthRetrofit") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGameApi(@Named("ApiRetrofit") retrofit: Retrofit): GameApi {
        return retrofit.create(GameApi::class.java)
    }

}