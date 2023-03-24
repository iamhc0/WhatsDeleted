package com.gmail.anubhavdas54.whatsdeleted.utils

import android.os.Environment
import android.os.FileObserver
import android.util.Log
import java.io.File

private const val TAG = "MediaObserver"
var ROOT_DIR = Environment.getExternalStorageDirectory().absolutePath
var ROOT_DIR_COPY_BASE_FOLDER = "/.whatsAppDeletedImages"
var ROOT_DIR_COPY =
    Environment.getExternalStorageDirectory().absolutePath + ROOT_DIR_COPY_BASE_FOLDER
var IMAGE_DIRECTORY = "WhatsApp Images"
var VIDEO_DIRECTORY = "WhatsApp Video"
var VOICE_DIRECTORY = "WhatsApp Voice Notes"

class MediaObserver : FileObserver(
    getWhatsappMediaFolder(MediaStatus.IMAGE), ALL_EVENTS
) {

    override fun onEvent(event: Int, path: String?) {
        Log.d(TAG, "onEvent-Code: $event")
        if (event == MOVED_TO) {
            Log.d(TAG, "onEvent: Move To")
            try {
                val srcFile = File(
                    getWhatsappMediaFolder(MediaStatus.IMAGE) + "${File.separator}$path"
                )
                val destFile:File = if (isRPlus())
                    File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        "WhatsDeleted${File.separator}WhatsDeleted Images${File.separator}$path"
                    )
                else {
                    File(
                        Environment.getExternalStorageDirectory(),
                        "WhatsDeleted${File.separator}WhatsDeleted Images${File.separator}$path"
                    )
                }
                srcFile.copyTo(
                    target = destFile,
                    overwrite = false,
                    bufferSize = DEFAULT_BUFFER_SIZE
                )
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }


}

enum class MediaStatus {
    IMAGE, VIDEO, VOICE,
}

private fun getWhatsappMediaFolder(mediaStatus: MediaStatus): String {
    return when (mediaStatus) {
        MediaStatus.IMAGE -> "$ROOT_DIR/$IMAGE_DIRECTORY"
        MediaStatus.VIDEO -> "$ROOT_DIR/$VIDEO_DIRECTORY"
        MediaStatus.VOICE -> "$ROOT_DIR/$VOICE_DIRECTORY"
    }


}


fun setWhatsAppRootFolderMedia() {
    var root = ""
    val target1 =
        File(Environment.getExternalStorageDirectory().absolutePath, "/WhatsApp/Media")
    val target2 = File(
        Environment.getExternalStorageDirectory().absolutePath,
        "/Android/media/com.whatsapp/WhatsApp/Media"
    )

    if (target1.exists() && target1.listFiles() != null && target1.listFiles().size > 1) {
        ROOT_DIR = Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media"
    } else if (target2.exists() && target2.listFiles() != null && target2.listFiles().size > 1) {
        ROOT_DIR =
            Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media"

    }


    if (isRPlus()) {
        ROOT_DIR_COPY_BASE_FOLDER = "/WhatsDeleted"
        ROOT_DIR_COPY =
            Environment.getExternalStorageDirectory().absolutePath + "/DCIM" + ROOT_DIR_COPY_BASE_FOLDER
    }

    Log.d(TAG, "ROOT_DIR: $ROOT_DIR")
    Log.d(TAG, "ROOT_DIR_COPY: $ROOT_DIR_COPY")


}
