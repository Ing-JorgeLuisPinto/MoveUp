package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RouteDao {
    @Query("SELECT * FROM saved_routes ORDER BY timestamp DESC")
    suspend fun getAll(): List<RouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: RouteEntity)

    @Delete
    suspend fun delete(route: RouteEntity)
}