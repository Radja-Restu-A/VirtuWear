package com.example.virtuwear.di

import com.example.virtuwear.data.service.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit") // Tandai Retrofit ini untuk Auth AP
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.20.10.3:8080/") // Ganti sesuai kebutuhan
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(@Named("AuthRetrofit") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}
