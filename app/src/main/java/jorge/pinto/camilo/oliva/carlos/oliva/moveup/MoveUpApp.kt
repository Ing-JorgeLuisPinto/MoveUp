package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.app.Application
import androidx.room.Room
import jorge.pinto.camilo.oliva.carlos.oliva.moveup.AppDatabase

class MoveUpApp : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "moveup_db"
        ).build()
    }
}
