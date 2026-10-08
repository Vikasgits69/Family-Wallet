package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.DocType
import org.json.JSONArray

class VaultTypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            if (data.startsWith("[")) {
                val jsonArray = JSONArray(data)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    list.add(jsonArray.getString(i))
                }
                list
            } else {
                data.split("|").filter { it.isNotBlank() }
            }
        } catch (e: Exception) {
            data.split("|").filter { it.isNotBlank() }
        }
    }

    @TypeConverter
    fun fromDocType(docType: DocType?): String {
        return docType?.name ?: DocType.OTHER.name
    }

    @TypeConverter
    fun toDocType(value: String?): DocType {
        if (value.isNullOrBlank()) return DocType.OTHER
        return try {
            DocType.valueOf(value)
        } catch (e: Exception) {
            DocType.OTHER
        }
    }
}
