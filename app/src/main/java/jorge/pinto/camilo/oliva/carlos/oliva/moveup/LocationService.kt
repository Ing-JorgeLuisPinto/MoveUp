package jorge.pinto.camilo.oliva.carlos.oliva.moveup

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val NOTIFICATION_CHANNEL_ID = "BUS_LOCATION_CHANNEL"
    private val NOTIFICATION_ID = 101

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START_LOCATION_SERVICE") {
            startLocationUpdates()
        } else if (intent?.action == "STOP_LOCATION_SERVICE") {
            stopLocationUpdates()
            stopSelf()
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        // CORRECCIÓN DE SEGURIDAD: VERIFICACIÓN DE PERMISOS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        @SuppressLint("MissingPermission")
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(10) // Frecuencia de actualización: 10 segundos
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(5))
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    uploadLocationToFirestore(location)
                }
            }
        }

        @SuppressLint("MissingPermission")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun stopLocationUpdates() {
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun uploadLocationToFirestore(location: Location) {
        val userId = auth.currentUser?.uid ?: return

        // NOTA: Se debe obtener el busNumber real del perfil del conductor, no usar un valor fijo como "16"
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to Timestamp.now(),
            "driverId" to userId,
            "busNumber" to "16"
        )

        db.collection("busLocations").document(userId)
            .set(locationData)
            .addOnFailureListener { }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Rastreo de Bus",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Servicio de Bus Activo")
            .setContentText("Tu ubicación se está transmitiendo en tiempo real.")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }
}