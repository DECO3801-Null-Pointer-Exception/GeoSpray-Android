package au.edu.uq.deco3801.nullpointerexception.geospray

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.DisplayRotationHelper

class ARArtActivity : ComponentActivity() {
    private lateinit var arSurfaceView: ARSurfaceView
    private lateinit var displayRotationHelper: DisplayRotationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayRotationHelper = DisplayRotationHelper(this)
        arSurfaceView = ARSurfaceView(this, displayRotationHelper)

        arSurfaceView.preserveEGLContextOnPause = true
        arSurfaceView.setEGLContextClientVersion(2)
        arSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        arSurfaceView.setRenderer(arSurfaceView)
        arSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        arSurfaceView.setWillNotDraw(false)

        setContentView(arSurfaceView)
    }

    override fun onResume() {
        super.onResume()
        arSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        arSurfaceView.onPause()
    }
}
