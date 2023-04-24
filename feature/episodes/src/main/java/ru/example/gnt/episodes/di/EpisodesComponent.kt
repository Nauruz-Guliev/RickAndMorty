package ru.example.gnt.episodes.di

import dagger.Component
import ru.example.gnt.episodes.presentation.episodes.EpisodesFragment

@Component(
    modules = [
        RepositoryModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        EpisodesDeps::class,
        EpisodeRouterDeps::class
    ]
)
internal interface EpisodesComponent {
    fun inject(fragment: EpisodesFragment)

    @Component.Builder
    interface Builder {
        fun mainDeps(deps: EpisodesDeps): Builder
        fun routerDeps(deps: EpisodeRouterDeps): Builder
        fun build(): EpisodesComponent
    }
}
