package ru.example.gnt.episodes.di.components

import dagger.Component
import ru.example.gnt.episodes.di.modules.RepositoryModule
import ru.example.gnt.episodes.di.modules.ViewModelModule
import ru.example.gnt.episodes.di.deps.EpisodesRouterDeps
import ru.example.gnt.episodes.di.deps.EpisodesDeps
import ru.example.gnt.episodes.presentation.episode_list.EpisodeListFragment

@Component(
    modules = [
        RepositoryModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        EpisodesDeps::class,
        EpisodesRouterDeps::class
    ]
)
internal interface EpisodesComponent {
    fun inject(fragment: EpisodeListFragment)

    @Component.Builder
    interface Builder {
        fun mainDeps(deps: EpisodesDeps): Builder
        fun routerDeps(deps: EpisodesRouterDeps): Builder
        fun build(): EpisodesComponent
    }
}
