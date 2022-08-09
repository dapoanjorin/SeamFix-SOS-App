package com.seamfix.sosapp.di

import com.seamfix.sosapp.networking.ApiInterface
import com.seamfix.sosapp.networking.RequestHandler
import com.seamfix.sosapp.networking.RetrofitCompat
import com.seamfix.sosapp.util.AppCoroutineDispatchers
import com.seamfix.sosapp.util.DefaultAppCoroutineDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @[Provides Singleton]
    fun providesRetrofit(): Retrofit = RetrofitCompat.getInstance()

    @[Provides Singleton]
    fun providesApiInterface(retrofit: Retrofit): ApiInterface =
        retrofit.create(ApiInterface::class.java)

    @[Provides Singleton]
    fun providesRequestHandler(client: ApiInterface) = RequestHandler(client)

    @[Provides Singleton]
    fun providesDefaultAppCoroutineDispatcher() =
        DefaultAppCoroutineDispatcher() as AppCoroutineDispatchers
}