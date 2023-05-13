package ru.example.gnt.data.di.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.di.remote.interceptor.ConnectivityInterceptor
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.LocationService


private const val BASE_URL = "https://rickandmortyapi.com/api/"

@Module
class NetworkModule {


    @Provides
    @ApplicationScope
    fun provideJsonAdapterFactory(): KotlinJsonAdapterFactory {
        return KotlinJsonAdapterFactory()
    }

    @Provides
    @ApplicationScope
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        return logging
    }

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(
        connectivityInterceptor: ConnectivityInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor())
            .addInterceptor(connectivityInterceptor)
            .build()
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
        return BASE_URL
    }

    @Provides
    @ApplicationScope
    fun provideRxJavaFactory(): RxJava3CallAdapterFactory {
        return RxJava3CallAdapterFactory.create()
    }

    @Provides
    @ApplicationScope
    fun provideEpisodeRetrofitInstance(
        @BaseUrl baseUrl: String,
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): ru.example.gnt.data.remote.service.EpisodeService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create()
    }


    @Provides
    @ApplicationScope
    fun provideLocationRetrofitInstance(
        @BaseUrl baseUrl: String,
        moshiConverterFactory: MoshiConverterFactory,
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory
    ): LocationService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(rxJava3CallAdapterFactory)
            .build()
            .create()
    }


    @ApplicationScope
    @Provides
    fun provideCharacterService(
        @BaseUrl baseUrl: String,
        moshiConverterFactory: MoshiConverterFactory,
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory
    ): CharacterService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(rxJava3CallAdapterFactory)
            .build()
            .create()
    }

}
