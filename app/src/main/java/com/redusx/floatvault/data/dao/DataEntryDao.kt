package com.redusx.floatvault.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.redusx.floatvault.data.model.DataEntry
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface DataEntryDao {

    @Query("SELECT * FROM data_entries ORDER BY updatedAt DESC")
    fun getAllEntries(): Flow<List<DataEntry>>

    @Query("SELECT * FROM data_entries WHERE category = :category ORDER BY updatedAt DESC")
    fun getEntriesByCategory(category: String): Flow<List<DataEntry>>

    @Query("SELECT * FROM data_entries WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Flow<List<DataEntry>>

    @Query("SELECT * FROM data_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DataEntry?

    @Query("SELECT * FROM data_entries WHERE title LIKE '%' || :query || '%' OR fieldsJson LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchEntries(query: String): Flow<List<DataEntry>>

    @Query("SELECT category, COUNT(*) as count FROM data_entries GROUP BY category")
    fun getCategoryCounts(): Flow<List<CategoryCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DataEntry): Long

    @Update
    suspend fun updateEntry(entry: DataEntry): Int

    @Delete
    suspend fun deleteEntry(entry: DataEntry): Int

    @Query("DELETE FROM data_entries WHERE id IN (:ids)")
    suspend fun deleteEntriesByIds(ids: List<Long>): Int

    @Query("UPDATE data_entries SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean, updatedAt: Long): Int

    @Query("DELETE FROM data_entries")
    suspend fun deleteAllEntries(): Int
}

data class CategoryCount(
    val category: String,
    val count: Int
)
