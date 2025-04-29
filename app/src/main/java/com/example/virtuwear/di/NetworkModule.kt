package com.example.virtuwear.di

import com.example.virtuwear.data.service.AuthService
import com.example.virtuwear.data.service.DoubleGarmentService
import com.example.virtuwear.data.service.KlingAiApiService
import com.example.virtuwear.data.service.SingleGarmentService
import com.example.virtuwear.data.service.UserService
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
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    @Named("BackendRetrofit")
    fun provideBackendRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8090/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(@Named("BackendRetrofit") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideSingleGarmentService(@Named("BackendRetrofit") retrofit: Retrofit): SingleGarmentService {
        return retrofit.create(SingleGarmentService::class.java)
    }

    @Provides
    @Singleton
    fun provideDoubleGarmentService(@Named("BackendRetrofit") retrofit: Retrofit): DoubleGarmentService {
        return retrofit.create(DoubleGarmentService::class.java)
    }

    @Provides
    @Singleton
    fun provideKlingAiApiService(@Named("BackendRetrofit") retrofit: Retrofit): KlingAiApiService {
        return retrofit.create(KlingAiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(@Named("BackendRetrofit") retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}
