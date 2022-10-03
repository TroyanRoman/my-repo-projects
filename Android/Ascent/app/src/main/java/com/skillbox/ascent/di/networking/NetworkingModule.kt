package com.skillbox.ascent.di.networking

import android.app.Application
import android.content.Context
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.di.preferences.AuthTokenPreference
import com.skillbox.ascent.data.ascent.api.AscentUserApi
import com.skillbox.ascent.di.qualifiers.AuthInterceptorQualifier
import com.skillbox.ascent.di.qualifiers.LoggingInterceptorQualifier
import com.skillbox.ascent.oauth_data.AuthInterceptor
import com.skillbox.ascent.utils.MoshiDateAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Provides
    @LoggingInterceptorQualifier
    fun providesLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @Provides
    @AuthInterceptorQualifier
    fun providesAuthInterceptor(
        authTokenPreference: AuthTokenPreference
    ): Interceptor {
        return AuthInterceptor(authTokenPreference)
    }


    @Provides
    @Singleton
    fun providesAuthService(@ApplicationContext context: Context): AuthorizationService {
        return AuthorizationService(context)
    }



    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @LoggingInterceptorQualifier loggingInterceptor: Interceptor,
        @AuthInterceptorQualifier authInterceptor: Interceptor,
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .addNetworkInterceptor(authInterceptor)
            .followRedirects(true)
            .build()

    }

    @Provides
    @Singleton
    fun providesDateAdapter() : MoshiDateAdapter = MoshiDateAdapter()

    @Provides
    @Singleton
    fun providesMoshi(adapter: MoshiDateAdapter) : Moshi = Moshi.Builder().add(adapter).build()


    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi

    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://www.strava.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesUserApi(retrofit: Retrofit): AscentUserApi = retrofit.create()


    @Provides
    @Singleton
    fun providesActivityApi(retrofit: Retrofit) : AscentActivityApi = retrofit.create()

}