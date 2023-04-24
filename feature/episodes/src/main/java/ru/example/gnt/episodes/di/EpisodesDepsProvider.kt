package ru.example.gnt.episodes.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

interface EpisodesDepsProvider {


    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: EpisodesDeps

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val routerDeps: EpisodeRouterDeps

    companion object : EpisodesDepsProvider by EpisodesDepsStore
}

object EpisodesDepsStore : EpisodesDepsProvider {
    override var deps: EpisodesDeps by Delegates.notNull()
    override var routerDeps: EpisodeRouterDeps by Delegates.notNull()
}

internal class EpisodesComponentViewModel : ViewModel() {
    val charactersComponent = DaggerEpisodesComponent.builder()
        .mainDeps(EpisodesDepsProvider.deps)
        .routerDeps(EpisodesDepsProvider.routerDeps)
        .build()
}
