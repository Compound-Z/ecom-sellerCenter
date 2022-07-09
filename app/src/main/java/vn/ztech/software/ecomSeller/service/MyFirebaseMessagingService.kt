package vn.ztech.software.ecomSeller.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.database.local.user.UserManager
import vn.ztech.software.ecomSeller.ui.main.MainActivity
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


const val TAG = "MyFirebaseMessagingService"
class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.data?.get("title").toString()
        val content = message.data?.get("content").toString()
        var orderId = message.data?.get("orderId").toString()
        val imageUrl = message.data?.get("imageUrl").toString()
        Log.d("xxx url", imageUrl)
        Log.d("xxx data", message.data.toString())
        val image = getBitmapFromUrl(imageUrl)
        Noti.showNoti(this, System.currentTimeMillis(), title, content, orderId, image)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "newTOken" + token)
        val userManager = UserManager.getInstance(this)
        userManager.saveNewFCMToken(token)
    }
    object Noti{
        fun showNoti(context: Context, timeStamp: Long, title: String, content: String, orderId: String, image: Bitmap?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Order notification"
                val descriptionText = "Channel for receive notis related to order services"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Create an explicit intent for an Activity in your app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            intent.putExtra("launchFromNoti", true)
            intent.putExtra("orderId", orderId)

            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            var builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_filled_location_on_24))
                .setContentTitle(title)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setShowWhen(true)
                .setWhen(timeStamp)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
//            image?.let {
//                builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(it))
//            }
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(Random.nextInt(999999), builder.build())
            }
        }
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}