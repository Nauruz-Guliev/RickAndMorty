package ru.example.gnt.data.di

import dagger.Module
import dagger.Provides
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.mapper.CharacterEntityResponseMapper
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper

@Module
class DataUtilityModule {

    @Provides
    fun provideCharacterEntityMapper(
        @BaseUrl baseUrl: String,
        urlIdExtractor: UrlIdExtractor
    ): CharacterEntityResponseMapper {
        return CharacterEntityResponseMapper(
            baseUrl = baseUrl,
            urlIdExtractor = urlIdExtractor
        )
    }

    @Provides
    fun provideUrlIdExtractor(): UrlIdExtractor {
        return UrlIdExtractor()
    }

    @Provides
    fun provideEpisodeEntityMapper(
        @BaseUrl baseUrl: String,
        urlIdExtractor: UrlIdExtractor
    ): EpisodeEntityResponseMapper {
        return EpisodeEntityResponseMapper(
            urlIdExtractor = urlIdExtractor,
            baseUrl = baseUrl
        )
    }

    @Provides
    fun provideLocationEntityMapper(
        @BaseUrl baseUrl: String,
        urlIdExtractor: UrlIdExtractor
    ): CharacterEntityResponseMapper {
        return CharacterEntityResponseMapper(
            baseUrl = baseUrl,
            urlIdExtractor = urlIdExtractor
        )
    }
}
