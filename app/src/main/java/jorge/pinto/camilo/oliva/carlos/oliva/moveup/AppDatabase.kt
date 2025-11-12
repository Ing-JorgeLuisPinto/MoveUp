package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RouteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao
}
