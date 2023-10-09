package com.example.yourdrive.data.cloud

interface ProvideHttpClientBuilder {
    fun provideHttpClientBuilder(): ProvideOkHttpClientBuilder
}