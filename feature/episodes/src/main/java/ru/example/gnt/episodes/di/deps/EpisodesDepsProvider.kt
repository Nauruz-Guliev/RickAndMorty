package ru.example.gnt.episodes.di.deps

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import ru.example.gnt.episodes.di.components.DaggerEpisodesComponent
import kotlin.properties.Delegates

interface EpisodesDepsProvider {


    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: EpisodesDeps

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val routerDeps: EpisodesRouterDependency

    companion object : EpisodesDepsProvider by EpisodesDepsStore
}

object EpisodesDepsStore : EpisodesDepsProvider {
    override var deps: EpisodesDeps by Delegates.notNull()
    override var routerDeps: EpisodesRouterDependency by Delegates.notNull()
}

internal class EpisodesComponentViewModel : ViewModel() {
    val episodesComponent = DaggerEpisodesComponent.builder()
        .mainDeps(EpisodesDepsProvider.deps)
        .routerDeps(EpisodesDepsProvider.routerDeps)
        .build()
}
