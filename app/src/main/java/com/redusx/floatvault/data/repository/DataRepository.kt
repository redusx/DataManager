package com.redusx.floatvault.data.repository

import com.redusx.floatvault.data.dao.CategoryCount
import com.redusx.floatvault.data.dao.DataEntryDao
import com.redusx.floatvault.data.model.DataEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val dao: DataEntryDao
) {

    fun getAllEntries(): Flow<List<DataEntry>> = dao.getAllEntries()

    fun getEntriesByCategory(category: String): Flow<List<DataEntry>> =
        dao.getEntriesByCategory(category)

    fun getFavorites(): Flow<List<DataEntry>> = dao.getFavorites()

    fun searchEntries(query: String): Flow<List<DataEntry>> = dao.searchEntries(query)

    fun getCategoryCounts(): Flow<List<CategoryCount>> = dao.getCategoryCounts()

    suspend fun getEntryById(id: Long): DataEntry? = dao.getEntryById(id)

    suspend fun insertEntry(entry: DataEntry): Long = dao.insertEntry(entry)

    suspend fun updateEntry(entry: DataEntry) = dao.updateEntry(
        entry.copy(updatedAt = System.currentTimeMillis())
    )

    suspend fun deleteEntry(entry: DataEntry) = dao.deleteEntry(entry)

    suspend fun deleteEntriesByIds(ids: List<Long>) = dao.deleteEntriesByIds(ids)

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) =
        dao.updateFavorite(id, !currentFavorite, System.currentTimeMillis())

    suspend fun deleteAllEntries() = dao.deleteAllEntries()
}
