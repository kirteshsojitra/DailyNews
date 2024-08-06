package com.project.dailynews.data.local

import androidx.room.TypeConverter
import com.project.dailynews.data.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}