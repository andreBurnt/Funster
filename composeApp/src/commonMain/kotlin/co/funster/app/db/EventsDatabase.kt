package co.funster.app.db

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import co.funster.app.core.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Data Access Object (DAO) for managing [Event] entities in the Room database.
 */
@Dao
interface EventDao {
    /**
     * Inserts or updates a list of events, replacing existing entries on conflict.
     *
     * @param events The list of [Event]s to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEvents(events: List<Event>)

    /**
     * Retrieves all events for a given city, sorted by start date in ascending order.
     * City comparison is case-insensitive.
     *
     * @param city The city to fetch events for.
     * @return A list of [Event]s matching the city.
     */
    @Query("SELECT * FROM events WHERE UPPER(city) = UPPER(:city) ORDER BY startDate ASC")
    suspend fun getEventsForCity(city: String): List<Event>

    /**
     * Deletes all events from the database.
     */
    @Query("DELETE FROM events")
    suspend fun clearAll()
}

/**
 * Room database for storing [Event] entities. Versioned and constructed with a multiplatform-compatible constructor.
 */
@Database(entities = [Event::class], version = 1, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .addMigrations()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getEventDao(appDatabase: AppDatabase) = appDatabase.eventDao()
