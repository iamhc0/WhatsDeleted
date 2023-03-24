package com.gmail.anubhavdas54.whatsdeleted.utils

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gmail.anubhavdas54.whatsdeleted.R
import java.io.File

/**
 * Created by Hamza Chaudhary
 * Sr. Software Engineer Android
 * Created on 22 Sep,2022 11:37
 * Copyright (c) All rights reserved.
 */

fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Suppress("SameParameterValue")
fun AppCompatActivity.createNotificationChannel(
    id: String,
    name: String,
    desc: String,
    importance: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val mChannel = NotificationChannel(id, name, importance)
        mChannel.description = desc
        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }
}


fun Context.deleteRecursive(f: File) {
    if (f.isDirectory) {
        for (child in f.listFiles()) {
            if (!child.deleteRecursively())
                Toast.makeText(
                    applicationContext,
                    getString(R.string.unable_to_delete, child.toString()),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}

fun Context.startObserverService() {
    // Media Observer Service
    val mediaObserverService = Intent(this, MediaObserverService::class.java)
    startService(mediaObserverService)

}

val pendingIntentIMMutable = if (isSPlus())
    PendingIntent.FLAG_IMMUTABLE
else
    PendingIntent.FLAG_UPDATE_CURRENT
