package com.loopin.app.detection.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.loopin.app.LoopInApplication

/**
 * Android NotificationListenerService that listens strictly on-device
 * for incoming notifications from banks and payment apps to detect
 * RBI-mandated pre-debit notices.
 *
 * Runs 100% locally:
 * - NO SMS permissions requested.
 * - NO bank credentials or statements needed.
 * - NO network transmission of notification data.
 */
class LoopInNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        try {
            val packageName = sbn.packageName ?: return
            val extras = sbn.notification?.extras ?: return

            val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
            val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

            val combinedBody = when {
                bigText.isNotBlank() && bigText.length > text.length -> bigText
                else -> text
            }

            if (title.isBlank() && combinedBody.isBlank()) return

            val app = applicationContext as? LoopInApplication ?: return
            app.container.detectionManager.processNotification(
                packageName = packageName,
                title = title,
                text = combinedBody,
                postTime = sbn.postTime
            )
        } catch (e: Exception) {
            Log.w("LoopInNotificationListener", "Failed to process notification safely", e)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("LoopInNotificationListener", "Notification listener connected successfully")
        (applicationContext as? LoopInApplication)?.container?.detectionManager?.checkPermission()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("LoopInNotificationListener", "Notification listener disconnected")
        (applicationContext as? LoopInApplication)?.container?.detectionManager?.checkPermission()
    }
}
