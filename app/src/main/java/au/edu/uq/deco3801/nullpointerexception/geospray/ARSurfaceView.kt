package au.edu.uq.deco3801.nullpointerexception.geospray

import android.app.Activity
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.DisplayRotationHelper
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.GeoSprayPermissionsHelper
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.BackgroundRenderer
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PlaneRenderer
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PointCloudRenderer
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Camera
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ARSurfaceView(context: Context, private val displayRotationHelper: DisplayRotationHelper) :
    GLSurfaceView(context), GLSurfaceView.Renderer{
    private val backgroundRenderer: BackgroundRenderer = BackgroundRenderer()
    private val planeRenderer: PlaneRenderer = PlaneRenderer()
    private val pointCloudRenderer: PointCloudRenderer = PointCloudRenderer()

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
                    Log.e(TAG, "Some error", e)
                }
            }

            else -> {
                Log.e(TAG, "ARCore is not supported on this device")
                // TODO: ARCore is not supported on this device
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::session.isInitialized) {
            val exception: Exception? = null
            val message: String? = null

            requestARCoreInstallation(context)

            if (!GeoSprayPermissionsHelper.hasGeoSprayPermissions(context)) {
                GeoSprayPermissionsHelper.requestPermissions(context)
            }

            try {
                Log.d(TAG, "Creating new session")
                // Create new session
                session = Session(context)

                // Configure the new session
                val config = Config(session)
                config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED
                config.focusMode = Config.FocusMode.AUTO
                session.configure(config)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create session", e)
            }


        }

        try {
            session.resume()
        } catch (e: CameraNotAvailableException) {
            // TODO: Show error
            Log.e(TAG, "Camera is not available", e)
            return
        }
        displayRotationHelper.onResume()
    }

    override fun onPause() {
        super.onPause()
        displayRotationHelper.onPause()
        session.pause()
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        try {
            backgroundRenderer.createOnGlThread(context)
            planeRenderer.createOnGlThread(context, "models/trigrid.png")
            pointCloudRenderer.createOnGlThread(context)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read an asset file", e)
        }
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        displayRotationHelper.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (!::session.isInitialized) return

        try {
            displayRotationHelper.updateSessionIfNeeded(session)

            session.setCameraTextureName(backgroundRenderer.textureId)

            val frame: Frame = session.update()
            val camera: Camera = frame.camera

            // TODO: Handle tap

            backgroundRenderer.draw(frame)

            // TODO: Tracking state

            // TODO: Camera matrix

            // TODO: Point cloud

            // TODO: Draw planes
        } catch (e: Exception) {
            Log.e(TAG, "Error on session", e)
        }
    }

    companion object {
        private const val TAG: String = "ARRenderer"
    }
}