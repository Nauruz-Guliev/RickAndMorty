package ru.example.gnt.common.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): ArrayList<String> {
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromArrayList(list: ArrayList<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}