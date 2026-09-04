package com.redusx.floatvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_entries")
data class DataEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val title: String,
    val fieldsJson: String,
    val iconName: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
