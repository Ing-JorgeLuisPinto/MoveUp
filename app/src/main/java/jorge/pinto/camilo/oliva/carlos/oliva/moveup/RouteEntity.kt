package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routes")
data class RouteEntity(
    @PrimaryKey val fileName: String,
    val name: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
