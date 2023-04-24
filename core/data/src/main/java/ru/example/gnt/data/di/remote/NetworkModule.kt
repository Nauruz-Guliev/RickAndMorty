package ru.example.gnt.data.di.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.di.qualifiers.BaseUrl
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
        return CharacterService.BASE_URL
    }

    @Provides
    @ApplicationScope
    fun provideRxJavaFactory(): RxJava3CallAdapterFactory {
        return RxJava3CallAdapterFactory.create()
    }

    @Provides
    @ApplicationScope
    fun provideRetrofit(
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
