package com.gmail.anubhavdas54.whatsdeleted.utils

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.io.File
import java.text.DateFormat
import java.util.*

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        /*This happens because WhatsApp and Gmail send a group summary notification alongside other notifications.
        * https://stackoverflow.com/questions/45890487/android-onnotificationposted-is-called-twice-for-gmail-and-whatsapp
        * */
        sbn?.let {
            if (it.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) {
                //Ignore the notification
                return
            }

        }

        if (sbn?.packageName == "com.whatsapp") {

            val date =
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
            val sender = sbn.notification.extras.getString("android.title")
            val msg = sbn.notification.extras.getString("android.text")

            File(this.filesDir, "msgLog.txt").appendText("$date | $sender: $msg\n")

        } else if (sbn?.packageName == "org.thoughtcrime.securesms") {

            val date =
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
            val sender = sbn.notification.extras.getString("android.title")
            val msg = sbn.notification.extras.getCharSequence("android.text")?.toString()

            File(this.filesDir, "signalMsgLog.txt").appendText("$date | $sender: $msg\n")
        }
    }
}
