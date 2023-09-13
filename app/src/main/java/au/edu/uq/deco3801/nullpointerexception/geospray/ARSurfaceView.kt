package au.edu.uq.deco3801.nullpointerexception.geospray

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.GeoSprayPermissionsHelper
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session

class ARSurfaceView(context: Context) : GLSurfaceView(context) {
    private lateinit var session: Session

    private fun requestARCoreInstallation(context: Context) {
        when (ArCoreApk.getInstance().checkAvailability(context)) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                // ARCore is installed
            }

            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    when (ArCoreApk.getInstance().requestInstall(context as Activity?, true)) {
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            // User has been requested to install ARCore
                        }

                        ArCoreApk.InstallStatus.INSTALLED -> {
                            // ARCore has been installed
                        }
                    }
                } catch (e: Exception) {
                    // TODO: Handle exception
                }
            }

            else -> {
                // TODO: ARCore is not supported on this device
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("FOO123", "test")
        if (!::session.isInitialized) {
            Log.d("FOO123", "is not initialised")
            val exception: Exception? = null
            val message: String? = null

            requestARCoreInstallation(context)

            if (!GeoSprayPermissionsHelper.hasGeoSprayPermissions(context)) {
                GeoSprayPermissionsHelper.requestPermissions(context)
            }

            // TODO: Create new session

            // TODO: Configure the new session

        }

        // TODO: Try resumming session
    }

}