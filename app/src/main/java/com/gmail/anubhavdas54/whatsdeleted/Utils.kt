package com.gmail.anubhavdas54.whatsdeleted

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Hamza Chaudhary
 * Sr. Software Engineer Android
 * Created on 22 Sep,2022 11:37
 * Copyright (c) All rights reserved.
 */


const val REQUEST_CODE_NOTIFICATION_PERMISSION = 222
fun AppCompatActivity.startNotificationAccess() {
    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
    startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_PERMISSION)
}

fun Context.hasNotificationAccess(): Boolean {
    val enabled = Settings.Secure.getString(
        contentResolver,
        "enabled_notification_listeners"
    )
    return enabled != null && enabled.contains(packageName)
}

