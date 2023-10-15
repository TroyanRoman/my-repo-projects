package com.example.yourdrive.core_add

import android.content.Context
import android.content.SharedPreferences
import com.example.yourdrive.presentation.authorization.data.ProvideEncryptedPreferences
import com.github.johnnysc.coremvvm.core.Dispatchers
import com.github.johnnysc.coremvvm.core.ManageResources
import com.github.johnnysc.coremvvm.data.ProvideConverterFactory
import com.github.johnnysc.coremvvm.data.ProvideInterceptor
import com.github.johnnysc.coremvvm.data.ProvideOkHttpClientBuilder
import com.github.johnnysc.coremvvm.data.ProvideRetrofitBuilder
import com.github.johnnysc.coremvvm.presentation.CanGoBack
import com.github.johnnysc.coremvvm.presentation.GlobalErrorCommunication
import com.github.johnnysc.coremvvm.presentation.ProgressCommunication
import com.github.johnnysc.coremvvm.sl.CoreModule
import retrofit2.Retrofit


class YouDriveCore(private val context: Context, isDebug: Boolean) : CoreModule,
    ProvideEncryptedPrefs {

    private val dispatchers = Dispatchers.Base()
    private val manageResources = ManageResources.Base(context)

    private val communication = GlobalErrorCommunication.Base()
    private val progress = ProgressCommunication.Base()
    private val canGoBack: CanGoBack.Callback = CanGoBack.Callback.Base()

    private val retrofitBuilder = ProvideRetrofitBuilder.Base(
        ProvideConverterFactory.Base(),
        ProvideOkHttpClientBuilder.Base(
            if (isDebug)
                ProvideInterceptor.Debug()
            else
                ProvideInterceptor.Release()
        )
    )

    private val tokenPrefs = if (isDebug) ProvideEncryptedPreferences.Debug(context) else
        ProvideEncryptedPreferences.Release(context)


    override fun string(id: Int): String = manageResources.string(id)

    override fun dispatchers(): Dispatchers = dispatchers

    override fun provideGlobalErrorCommunication(): GlobalErrorCommunication.Mutable =
        communication

    override fun provideProgressCommunication(): ProgressCommunication.Mutable = progress

    override fun provideRetrofitBuilder(): Retrofit.Builder =
        retrofitBuilder.provideRetrofitBuilder()

    override fun sharedPreferences(key: String): SharedPreferences =
        context.getSharedPreferences(key, Context.MODE_PRIVATE)

    override fun tokenPreferences(key: String): SharedPreferences =
        tokenPrefs.sharedPreferences(key)

    override fun provideCanGoBack(): CanGoBack.Callback = canGoBack

}


interface ProvideEncryptedPrefs {
    fun tokenPreferences(key: String): SharedPreferences
}