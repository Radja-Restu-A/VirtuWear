package com.example.virtuwear.di

import com.example.virtuwear.data.service.ImagebbApiService
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
object ImageBBModule {

    @Provides
    @Singleton
    @Named("ImgBBRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/1/") // Base URL ImgBB
            .client(okHttpClient) // Pakai OkHttpClient dari NetworkModule
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideImgBBApi(@Named("ImgBBRetrofit") retrofit: Retrofit): ImagebbApiService {
        return retrofit.create(ImagebbApiService::class.java)
    }
}
