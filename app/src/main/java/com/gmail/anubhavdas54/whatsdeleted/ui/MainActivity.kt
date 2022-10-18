package com.gmail.anubhavdas54.whatsdeleted.ui

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.gmail.anubhavdas54.whatsdeleted.R
import com.gmail.anubhavdas54.whatsdeleted.databinding.ActivityMainBinding
import com.gmail.anubhavdas54.whatsdeleted.utils.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val activity: AppCompatActivity = this
    private val TAG: String = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.run {
            init()
        }

    }


    private fun ActivityMainBinding.init() {
        setWhatsAppRootFolderMedia()
        initListener()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel(
                "mediaObserver",
                "Media Observer",
                "Watches the default WhatsApp directories for new media",
                NotificationManager.IMPORTANCE_LOW
            )
        } else {
            @Suppress("DEPRECATION")
            createNotificationChannel(
                "mediaObserver",
                "Media Observer",
                "Watches the default WhatsApp directories for new media",
                Notification.PRIORITY_LOW
            )
        }

        if (!checkPermissionsWithPersisted(true))
            requestForPermissionsPersisted(activityResultLauncher, true)


    }

    private fun ActivityMainBinding.initListener() {
        viewWaLogBtn.setOnClickListener {
            val intent = Intent(activity, MsgLogViewerActivity::class.java)
            intent.putExtra("app", "whatsapp")
            startActivity(intent)
        }

        viewSignalLogBtn.setOnClickListener {
            val intent = Intent(activity, MsgLogViewerActivity::class.java)
            intent.putExtra("app", "signal")
            startActivity(intent)
        }

        imgDirDelBtn.setOnClickListener {
            AlertDialogHelper.showDialog(
                this@MainActivity,
                getString(R.string.del_backup_img),
                getString(R.string.del_backup_img_confirm),
                getString(R.string.yes),
                getString(R.string.cancel)
            ) { _, _ ->
                try {
                    deleteRecursive(File(ROOT_DIR_COPY))
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.del_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        medObsSwitch.isChecked = isServiceRunning(MediaObserverService::class.java)
        medObsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startObserverService()
                Toast.makeText(applicationContext, getString(R.string.started), Toast.LENGTH_SHORT)
                    .show()
            } else {
                startObserverService()
                Toast.makeText(applicationContext, getString(R.string.stopped), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val isNotificationListenerService = hasNotificationAccess()
        notificationListenerSwitch.isChecked = isNotificationListenerService
        notificationListenerSwitch.isClickable = false
        test.setOnClickListener {
            if (!notificationListenerSwitch.isChecked) {
                startNotificationAccess()
            }

        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Log.d(TAG, "PERMISSIONS PERMANENTLY DENIED")

                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )

                        ) {
                            //rmissionHelper.showSettingsDialog(this)
                        } else {
                            requestForPermissionsPersisted(activityResultLauncher)
                        }

                    }
                } else {
                    Log.d(TAG, "PERMISSIONS NOT GRANTED SHOW PERMISSIONS RATIONAL")

                }
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_toggle_theme -> {

                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            binding?.run {
                notificationListenerSwitch.isChecked = hasNotificationAccess()
            }


        }
    }


    var activityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data!!
            Log.d("HEY: ", data.data.toString())
            data.data?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )


            }

        }
    }

}
