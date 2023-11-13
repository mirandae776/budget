package edu.msoe.budget_app.entities

import androidx.room.TypeConverter
import java.util.Date

class Converter {
    @TypeConverter
    fun fromDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toDate(date: Date?): Long? {
        return date?.time
    }
}
