package au.edu.uq.deco3801.nullpointerexception.geospray

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.BackgroundRenderer
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PlaneRenderer
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PointCloudRenderer
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ARRenderer(private val context: Context) : GLSurfaceView.Renderer{

    private val backgroundRenderer: BackgroundRenderer = BackgroundRenderer()
    private val planeRenderer: PlaneRenderer = PlaneRenderer()
    private val pointCloudRenderer: PointCloudRenderer = PointCloudRenderer()

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        try {
            backgroundRenderer.createOnGlThread(context)
            planeRenderer.createOnGlThread(context, "models/trigrid.png")
            pointCloudRenderer.createOnGlThread(context)
        } catch (e: IOException) {
            Log.e(Companion.TAG, "Failed to read an asset file", e)
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        // TODO("Not yet implemented")
    }

    companion object {
        private const val TAG: String = "ARRenderer"
    }
}