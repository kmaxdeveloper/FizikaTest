package uz.kmax.fizikatest.data.manager

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            val title = it.title ?: "Fizika Test"
            val message = it.body ?: ""
            NotificationHelper(applicationContext).showNotification(title, message)
        } ?: run {
            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"] ?: "Fizika Test"
                val message = remoteMessage.data["message"] ?: ""
                NotificationHelper(applicationContext).showNotification(title, message)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
