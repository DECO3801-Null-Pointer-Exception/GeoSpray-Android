package au.edu.uq.deco3801.nullpointerexception.geospray.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/** Helper to ask camera permission.  */
object GeoSprayPermissionsHelper {
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)

    /** Check to see we have the necessary permissions for this app.  */
    fun hasGeoSprayPermissions(context: Context): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
    fun requestPermissions(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity, PERMISSIONS, 0)
    }

    /** Check to see if we need to show the rationale for this permission.  */
    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return PERMISSIONS.any {  ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
    }

    /** Launch Application Setting to grant permission.  */
    fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}