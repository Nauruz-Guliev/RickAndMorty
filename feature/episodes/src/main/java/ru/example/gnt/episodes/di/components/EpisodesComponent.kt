package ru.example.gnt.episodes.di.components

import dagger.Component
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.episodes.di.modules.RepositoryModule
import ru.example.gnt.episodes.di.modules.ViewModelModule
import ru.example.gnt.episodes.di.deps.EpisodesRouterDependency
import ru.example.gnt.episodes.di.deps.EpisodesDeps
import ru.example.gnt.episodes.presentation.episode_details.EpisodeDetailsFragment
import ru.example.gnt.episodes.presentation.episode_list.EpisodeListFragment

@Component(
    modules = [
        RepositoryModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        EpisodesDeps::class,
        EpisodesRouterDependency::class
    ]
)
@ScreenScope
internal interface EpisodesComponent {
    fun inject(fragment: EpisodeListFragment)
    fun inject(fragment: EpisodeDetailsFragment)
    @Component.Builder
    interface Builder {
        fun mainDeps(deps: EpisodesDeps): Builder
        fun routerDeps(deps: EpisodesRouterDependency): Builder
        fun build(): EpisodesComponent
    }
}
