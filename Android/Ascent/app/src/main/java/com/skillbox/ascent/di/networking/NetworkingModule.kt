package com.skillbox.ascent.di.networking

import android.app.Application
import android.content.Context
import android.util.Log
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.di.AppAuth
import com.skillbox.ascent.di.AuthTokenPreference
import com.skillbox.ascent.data.ascent.api.AscentApi
import com.skillbox.ascent.di.UserDataPreferences
import com.skillbox.ascent.oauth_data.AuthFailedInterceptor
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
        val logingInterceptor =  HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        Log.d("Networking","provides loging interceptor")
        return  logingInterceptor
    }


    @Provides
    @AuthInterceptorQualifier
    fun providesAuthInterceptor(
        authTokenPreference: AuthTokenPreference
    ): Interceptor {
        val authInterceptor =  AuthInterceptor(authTokenPreference)
        Log.d("Networking","provides auth interceptor")
        return authInterceptor
    }



    @Provides
    @AuthFailedInterceptorQualifier
    fun providesAuthFailedInterceptor(
        authTokenPreference: AuthTokenPreference,
        appAuth: AppAuth,
        authService: AuthorizationService

    ): Interceptor = AuthFailedInterceptor(
        authPrefs = authTokenPreference,
        appAuth = appAuth,
        authService = authService
    )



    @Provides
    @Singleton
    fun provideMyPreference(@ApplicationContext appContext: Context): AuthTokenPreference {
        Log.d("Networking","provides auth token prefs")
        return AuthTokenPreference(appContext)
    }

    //временно пока не сделаю бд
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext appContext: Context)
    = UserDataPreferences(appContext)

    @Provides
    @Singleton
    fun providesAuthService(appAuth: AppAuth): AuthorizationService {
        val authService = appAuth.getAuthService()
        Log.d("Networking","provides auth service")
        return authService
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
        @AuthFailedInterceptorQualifier authFailedInterceptor: Interceptor
    ): OkHttpClient {

        val okHttpClient =  OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .addNetworkInterceptor(authInterceptor)
            .addNetworkInterceptor(authFailedInterceptor)
            .followRedirects(true)
            .build()
       //Timber.tag("OkHttpClient").d("Provides ok http $okHttpClient")
       Log.d("Networking","Provides ok http $okHttpClient")
       return okHttpClient

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

    ) : Retrofit {
        val retrofit =  Retrofit.Builder()
            .baseUrl("https://www.strava.com/api/v3/")
           // .addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
        Log.d("Networking","Provides retrofit $retrofit")
        return retrofit
    }

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit): AscentApi {
        val api = retrofit.create<AscentApi>()
        Log.d("Networking","Provides ascent api $api")
        return api
    }

    @Provides
    @Singleton
    fun providesActivityApi(retrofit: Retrofit) : AscentActivityApi = retrofit.create()

}