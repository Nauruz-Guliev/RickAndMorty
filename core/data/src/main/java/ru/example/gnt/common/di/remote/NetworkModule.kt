package ru.example.gnt.common.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.example.gnt.common.data.remote.RickAndMortyApi
import ru.example.gnt.common.di.qualifiers.BaseUrl
import ru.example.gnt.common.di.scope.ApplicationScope


@Module
class NetworkModule {
    @Provides
    @ApplicationScope
    fun provideJsonAdapterFactory(): KotlinJsonAdapterFactory {
        return KotlinJsonAdapterFactory()
    }

    @Provides
    @ApplicationScope
    fun provideMoshi(kotlinJsonAdapterFactory: KotlinJsonAdapterFactory): Moshi {
        return Moshi.Builder()
            .add(kotlinJsonAdapterFactory)
            .build()
    }

    @Provides
    @ApplicationScope
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @ApplicationScope
    @BaseUrl
    fun provideBaseUrl(): String {
        return RickAndMortyApi.BASE_URL
    }

    @Provides
    @ApplicationScope
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        moshiConverterFactory: MoshiConverterFactory
    ): RickAndMortyApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create()
    }

}
