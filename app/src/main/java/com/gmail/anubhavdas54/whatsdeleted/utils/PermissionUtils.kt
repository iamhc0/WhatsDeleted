package com.gmail.anubhavdas54.whatsdeleted.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by Hamza Chaudhary
 * Sr. Software Engineer Android
 * Created on 14 Jun,2022 11:25
 * Copyright (c) All rights reserved.
 */


const val REQUEST_CODE_NOTIFICATION_PERMISSION = 222
const val PERMISSION_WRITE_STORAGE = 111
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R


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



fun AppCompatActivity.requestForPermissionsPersisted(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    check: Boolean = false
) {
    // If Android 10+
    if (isRPlus()) {
        if (check)
            requestPermissionQ(activityResultLauncher)
    } else
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_WRITE_STORAGE
        )
}


@RequiresApi(Build.VERSION_CODES.Q)
fun AppCompatActivity.requestPermissionQ(activityResultLauncher: ActivityResultLauncher<Intent>) {
    val sm = getSystemService(Context.STORAGE_SERVICE) as StorageManager
    val intent =
        sm.primaryStorageVolume.createOpenDocumentTreeIntent()

    val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia"
    var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
    var scheme = uri.toString()
    scheme = scheme.replace("/root/", "/document/")
    scheme += "%3A$startDir"
    uri = Uri.parse(scheme)
    Log.d("URI", uri.toString())
    intent.putExtra("android.provider.extra.INITIAL_URI", uri)
    intent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    activityResultLauncher.launch(intent)
}

private val PERMISSIONS = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

fun AppCompatActivity.checkPermissionsWithPersisted(checkPersistedPermission: Boolean = false): Boolean {
    if (isRPlus()) {
        if (checkPersistedPermission) {
            return contentResolver.persistedUriPermissions.size > 0
        }
        return true
    } else {
        for (permissions in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    permissions!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

}

var actionOnPermission: ((granted: Boolean) -> Unit)? = null
 fun Context.handlePermission(
    permissionId: String,
    callback: (granted: Boolean) -> Unit, permissionRequestLauncher: ActivityResultLauncher<String>
) {
    actionOnPermission = null
    if (hasPermission(permissionId)) {
        callback(true)
    } else {
        actionOnPermission = callback
        permissionRequestLauncher.launch(permissionId)
    }
}


fun Context.hasPermission(permId: String) = ContextCompat.checkSelfPermission(
    this,
    permId
) == PackageManager.PERMISSION_GRANTED


fun Context.checkPermissions(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    // No explanation needed; request the permission
    //            Permission has already been granted
}


