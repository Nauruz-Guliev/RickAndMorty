package ru.example.gnt.locations

interface LocationsRouter {
    fun navigateToLocationDetails(id: Int)
    fun navigateToLocationList()
    fun navigateToCharacterDetails(id: Int)
}
