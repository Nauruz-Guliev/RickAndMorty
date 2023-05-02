package ru.example.gnt.locations.di

import android.content.Context
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.LocationEntityResponseMapper
import ru.example.gnt.data.remote.service.LocationService

interface LocationDependencies {
    val locationService: LocationService
    val locationsDao: LocationsDao
    val locationMapper: LocationEntityResponseMapper
    val context: Context
}
