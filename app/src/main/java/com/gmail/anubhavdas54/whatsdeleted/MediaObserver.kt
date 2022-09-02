package com.gmail.anubhavdas54.whatsdeleted

import android.os.Environment
import android.os.FileObserver
import android.util.Log
import java.io.File

private const val TAG = "MediaObserver"


class MediaObserver : FileObserver(
    File(
        getWhatsappRootFolder() + "${File.separator}WhatsApp Images"
    ).toString(), ALL_EVENTS
) {

    override fun onEvent(event: Int, path: String?) {

        if (event == MOVED_TO) {
            try {
                val srcFile = File(
                    getWhatsappRootFolder() + "${File.separator}WhatsApp Images${File.separator}$path"
                )
                val destFile = File(
                    Environment.getExternalStorageDirectory(),
                    "WhatsDeleted${File.separator}WhatsDeleted Images${File.separator}$path"
                )
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


private fun getWhatsappRootFolder(): String {
    var root = ""
    val target1 =
        File(Environment.getExternalStorageDirectory().absolutePath, "/WhatsApp/Media")
    val target2 = File(
        Environment.getExternalStorageDirectory().absolutePath,
        "/Android/media/com.whatsapp/WhatsApp/Media"
    )

    if (target1.exists() && target2.listFiles() != null && target1.listFiles().size > 1) {
        root = Environment.getExternalStorageDirectory().absolutePath+"/WhatsApp/Media"
    } else if (target2.exists() && target2.listFiles() != null && target2.listFiles().size > 1) {
        root =
            Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media"

    }
    return root

}
