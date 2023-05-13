package ru.example.gnt.episodes

interface EpisodesRouter {
    fun navigateToEpisodeDetails(id: Int?)
    fun navigateToCharacterDetails(id: Int)
}
