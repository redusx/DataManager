package com.example.datamanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.datamanager.data.dao.DataEntryDao
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.security.CryptoManager
import net.sqlcipher.database.SupportFactory

@Database(entities = [DataEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dataEntryDao(): DataEntryDao

    companion object {
        private const val DATABASE_NAME = "datamanager_encrypted.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, cryptoManager: CryptoManager): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, cryptoManager).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, cryptoManager: CryptoManager): AppDatabase {
            val passphrase = cryptoManager.getOrCreateDbPassphrase(context)
            val factory = SupportFactory(passphrase)

            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()

            // Proactively verify the database can be decrypted
            return try {
                db.openHelper.writableDatabase
                db
            } catch (e: Exception) {
                // If database was encrypted with an old/incompatible key from previous run, recover gracefully
                try {
                    db.close()
                } catch (closeEx: Exception) {
                    // Ignore
                }
                context.deleteDatabase(DATABASE_NAME)

                val freshDb = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()

                try {
                    freshDb.openHelper.writableDatabase
                } catch (ex: Exception) {
                    // Ignore
                }
                freshDb
            }
        }

        fun destroyInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
