/*
 * Copyright 2019 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.ar.core.Anchor;
import com.google.ar.core.Anchor.CloudAnchorState;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Config.CloudAnchorMode;
import com.google.ar.core.Frame;
import com.google.ar.core.Future;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Point.OrientationMode;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.CameraPermissionHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.DisplayRotationHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.LocationPermissionHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.ResolveDialogFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.TapHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.TrackingStateHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.BackgroundRenderer;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.ObjectRenderer;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PlaneRenderer;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PointCloudRenderer;

/**
 * Main Fragment for the Cloud Anchors Codelab.
 *
 * <p>This is where the AR Session and the Cloud Anchors are managed.
 */
public class CloudAnchorFragment extends Fragment implements GLSurfaceView.Renderer {
  private static final String TAG = CloudAnchorFragment.class.getSimpleName();

  // Rendering. The Renderers are created here, and initialized when the GL surface is created.
  private GLSurfaceView surfaceView;

  private boolean installRequested;

  private Session session;
  private DisplayRotationHelper displayRotationHelper;
  private TrackingStateHelper trackingStateHelper;
  private TapHelper tapHelper;
  private FirebaseManager firebaseManager;

  private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
  private final ObjectRenderer virtualObject = new ObjectRenderer();
  private final PlaneRenderer planeRenderer = new PlaneRenderer();
  private final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();

  // Temporary matrix allocated here to reduce number of allocations for each frame.
  private final float[] anchorMatrix = new float[16];

  @Nullable
  private Anchor currentAnchor = null;
  @Nullable
  private Future future = null;

  private ImageButton uploadButton;

  private StorageReference storageReference;
  private UploadTask uploadTask;
  private Bitmap image;
  private SeekBar rotationBar;
  private SeekBar scaleBar;
  private int imageRotation = -135;
  private float imageScale = 1.0f;
  private boolean visualise = true;
  private float imageWidth;
  private float imageHeight;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private double latitude;
  private double longitude;
  private byte[] preview;

  private Bitmap chosenImage;

  // Arguments from upload page
  private byte[] imageBytes;
  private String title;
  private String description;
  private int shortCode;

  private boolean resolved = false;

  private Toast currentToast;

  private FirebaseUser mAuth;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    tapHelper = new TapHelper(context);
    trackingStateHelper = new TrackingStateHelper(requireActivity());
    firebaseManager = new FirebaseManager(context);

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    storageReference = firebaseStorage.getReference();

    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
  }

  @Override
  public View onCreateView(
          LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    // Inflate from the Layout XML file.
    View rootView = inflater.inflate(R.layout.cloud_anchor_fragment, container, false);
    GLSurfaceView surfaceView = rootView.findViewById(R.id.surfaceView);
    this.surfaceView = surfaceView;
    displayRotationHelper = new DisplayRotationHelper(requireContext());
    surfaceView.setOnTouchListener(tapHelper);

    surfaceView.setPreserveEGLContextOnPause(true);
    surfaceView.setEGLContextClientVersion(2);
    surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
    surfaceView.setRenderer(this);
    surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    surfaceView.setWillNotDraw(false);

    // Retrieve arguments
    Bundle args = this.getArguments();

    if (args != null) {
      imageBytes = args.getByteArray("image");
      title = args.getString("title");
      description = args.getString("description");

      shortCode = args.getInt("shortcode");

      if (imageBytes != null) {
        chosenImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
      }
    }

    ImageButton rectButton = rootView.findViewById(R.id.rect_button);

    ImageButton shutterButton = rootView.findViewById(R.id.shutter_button);
    shutterButton.setOnClickListener(v -> onShutterButtonPressed());

    ImageButton clearButton = rootView.findViewById(R.id.clear_button);
    clearButton.setOnClickListener(v -> onClearButtonPressed());

    ImageButton backButton = rootView.findViewById(R.id.camera_page_back);
    backButton.setOnClickListener(view -> {
      if (future != null) {
        future.cancel();
      }

      getParentFragmentManager().popBackStack();
    });

    rotationBar = rootView.findViewById(R.id.rotation_seekbar);
    rotationBar.setOnSeekBarChangeListener(rotationChangeListener);
    rotationBar.setEnabled(false);

    scaleBar = rootView.findViewById(R.id.size_seekbar);
    scaleBar.setOnSeekBarChangeListener(scaleChangeListener);
    scaleBar.setEnabled(false);

    uploadButton = rootView.findViewById(R.id.upload_button);
    uploadButton.setOnClickListener(v -> onUploadButtonPressed());
    uploadButton.setEnabled(false);

    if (shortCode != 0) {
      rotationBar.setVisibility(View.GONE);
      scaleBar.setVisibility(View.GONE);
      uploadButton.setVisibility(View.GONE);
      clearButton.setVisibility(View.GONE);
      rectButton.setVisibility(View.GONE);
    }

    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();

    if (session == null) {
      Exception exception = null;
      String message = null;
      try {
        switch (ArCoreApk.getInstance().requestInstall(requireActivity(), !installRequested)) {
          case INSTALL_REQUESTED:
            installRequested = true;
            return;
          case INSTALLED:
            break;
        }

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
          CameraPermissionHelper.requestCameraPermission(requireActivity());
          return;
        }

        if (!LocationPermissionHelper.hasLocationPermission(requireActivity())) {
          LocationPermissionHelper.requestLocationPermission(requireActivity());
          return;
        }

        // Create the session.
        session = new Session(requireActivity());

        // Configure the session.
        Config config = new Config(session);
        config.setCloudAnchorMode(CloudAnchorMode.ENABLED);
        session.configure(config);

      } catch (UnavailableArcoreNotInstalledException
               | UnavailableUserDeclinedInstallationException e) {
        message = "Please install ARCore.";
        exception = e;
      } catch (UnavailableApkTooOldException e) {
        message = "Please update ARCore.";
        exception = e;
      } catch (UnavailableSdkTooOldException e) {
        message = "Please update this app.";
        exception = e;
      } catch (UnavailableDeviceNotCompatibleException e) {
        message = "This device does not support AR.";
        exception = e;
      } catch (Exception e) {
        message = "Failed to create AR session.";
        exception = e;
      }

      if (message != null) {
        String finalMessage = message;
        requireActivity().runOnUiThread(() -> showToast(finalMessage));
        Log.e(TAG, "Exception creating session.", exception);
        return;
      }
    }

    // Note that order matters - see the note in onPause(), the reverse applies here.
    try {
      session.resume();
    } catch (CameraNotAvailableException e) {
      requireActivity().runOnUiThread(() -> showToast("Camera not available. Try restarting the app."));
      session = null;
      return;
    }

    surfaceView.onResume();
    displayRotationHelper.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (session != null) {
      // Note that the order matters - GLSurfaceView is paused first so that it does not try
      // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
      // still call session.update() and get a SessionPausedException.
      displayRotationHelper.onPause();
      surfaceView.onPause();
      session.pause();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
    if (!LocationPermissionHelper.hasLocationPermission(requireActivity())) {
      requireActivity().runOnUiThread(() -> showToast("Location permission is needed to run this application."));
      if (!LocationPermissionHelper.shouldShowRequestPermissionRationale(requireActivity())) {
        // Permission denied with checking "Do not ask again".
        LocationPermissionHelper.launchPermissionSettings(requireActivity());
      }
      requireActivity().finish();
    }

    if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
      requireActivity().runOnUiThread(() -> showToast("Camera permission is needed to run this application."));
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(requireActivity())) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(requireActivity());
      }
      requireActivity().finish();
    }
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

    // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
    try {
      // Create the texture and pass it to ARCore session to be filled during update().
      backgroundRenderer.createOnGlThread(getContext());
      planeRenderer.createOnGlThread(getContext(), "models/trigrid.png");
      pointCloudRenderer.createOnGlThread(getContext());

      virtualObject.createOnGlThread(getContext());
      virtualObject.setMaterialProperties(1.0f, 0.0f, 0.0f, 0.0f);
    } catch (IOException e) {
      Log.e(TAG, "Failed to read an asset file.", e);
    }

    if (!resolved) {
      if (shortCode != 0) {
        onShortCodeEntered(shortCode);
      }
      resolved = true;
    }
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    displayRotationHelper.onSurfaceChanged(width, height);
    GLES20.glViewport(0, 0, width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clear screen to notify driver it should not load any pixels from previous frame.
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    if (session == null) {
      return;
    }
    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(session);

    try {
      session.setCameraTextureName(backgroundRenderer.getTextureId());

      // Obtain the current frame from ARSession. When the configuration is set to
      // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
      // camera framerate.
      Frame frame = session.update();
      Camera camera = frame.getCamera();

      // Handle one tap per frame.
      handleTap(frame, camera);

      // If frame is ready, render camera preview image to the GL surface.
      backgroundRenderer.draw(frame);

      // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
      trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

      // If not tracking, don't draw 3D objects, show tracking failure reason instead.
      if (camera.getTrackingState() == TrackingState.PAUSED) {
        requireActivity().runOnUiThread(() -> showToast(TrackingStateHelper.getTrackingFailureReasonString(camera)));
        return;
      }

      // Get projection matrix.
      float[] projmtx = new float[16];
      camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

      // Get camera matrix and draw.
      float[] viewmtx = new float[16];
      camera.getViewMatrix(viewmtx, 0);

      // Compute lighting from average intensity of the image.
      // The first three components are color scaling factors.
      // The last one is the average pixel intensity in gamma space.
      final float[] colorCorrectionRgba = new float[4];
      frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

      // Visualize tracked points.
      // Use try-with-resources to automatically release the point cloud.
      if (visualise) {
        try (PointCloud pointCloud = frame.acquirePointCloud()) {
          pointCloudRenderer.update(pointCloud);
          pointCloudRenderer.draw(viewmtx, projmtx);
        }
      }

      // No tracking error at this point. If we didn't detect any plane, show searchingPlane message.
      if (!hasTrackingPlane()) {
        requireActivity().runOnUiThread(() -> showToast("Searching for surfaces..."));
      }

      // Visualize planes.
      if (visualise) {
        planeRenderer.drawPlanes(
                session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);
      }

      if (currentAnchor != null && currentAnchor.getTrackingState() == TrackingState.TRACKING) {
        currentAnchor.getPose().toMatrix(anchorMatrix, 0);
        // Update and draw the model and its shadow.
        virtualObject.updateModelMatrix(anchorMatrix, imageScale);

        // Modify image properties
        virtualObject.rotateImage(imageRotation);
        virtualObject.updateTexture(image);
        float ratio = imageHeight / imageWidth;
        virtualObject.resizeImage(0.25f, 0.25f * ratio);

        // Draw image
        virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba);
      }
    } catch (Throwable t) {
      // Avoid crashing the application due to unhandled exceptions.
      Log.e(TAG, "Exception on the OpenGL thread.", t);
    }
  }

  // Handle only one tap per frame, as taps are usually low frequency compared to frame rate.
  private void handleTap(Frame frame, Camera camera) {
    if (currentAnchor != null || shortCode != 0) {
      return; // Do nothing if there was already an anchor.
    }

    MotionEvent tap = tapHelper.poll();
    if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
      for (HitResult hit : frame.hitTest(tap)) {
        // Check if any plane was hit, and if it was hit inside the plane polygon
        Trackable trackable = hit.getTrackable();
        // Creates an anchor if a plane or an oriented point was hit.
        if ((trackable instanceof Plane
                && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())
                && (PlaneRenderer.calculateDistanceToPlane(hit.getHitPose(), camera.getPose()) > 0))
                || (trackable instanceof Point
                && ((Point) trackable).getOrientationMode()
                == OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
          // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.

          // Adding an Anchor tells ARCore that it should track this position in
          // space. This anchor is created on the Plane to place the 3D model
          // in the correct position relative both to the world and to the plane.
          image = chosenImage;
          imageHeight = chosenImage.getHeight();
          imageWidth = chosenImage.getWidth();

          currentAnchor = hit.createAnchor();
          requireActivity().runOnUiThread(() -> {
            uploadButton.setEnabled(true);
            scaleBar.setEnabled(true);
            rotationBar.setEnabled(true);
          });

          visualise = false;

          break;
        }
      }
    }
  }

  /**
   * Checks if we detected at least one plane.
   */
  private boolean hasTrackingPlane() {
    for (Plane plane : session.getAllTrackables(Plane.class)) {
      if (plane.getTrackingState() == TrackingState.TRACKING) {
        return true;
      }
    }
    return false;
  }

  private void onClearButtonPressed() {
    // Clear the anchor from the scene.
    if (currentAnchor != null) {
      currentAnchor.detach();
      currentAnchor = null;
    }

    // Cancel any ongoing async operations.
    if (future != null) {
      future.cancel();
      future = null;
    }

    visualise = true;
    image = null;

    rotationBar.setProgress(180);
    scaleBar.setProgress(100);

    uploadButton.setEnabled(false);
  }

  private void onUploadButtonPressed() {
    if (!hasTrackingPlane()) {
      requireActivity().runOnUiThread(() -> showToast("Please point the camera at the image."));
      return;
    }

    requireActivity().runOnUiThread(() -> showToast("Uploading image..."));
    future = session.hostCloudAnchorAsync(currentAnchor, 300, this::onHostComplete);

    requireActivity().runOnUiThread(() -> {
      uploadButton.setEnabled(false);
      rotationBar.setEnabled(false);
      scaleBar.setEnabled(false);
    });

    // Required to stop getCurrentLocation complaining
    if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      requireActivity().runOnUiThread(() -> showToast("Location permission is needed to run this application."));
      return;
    }

    fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(
            requireActivity(), location -> {
              if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
              }
            }
    );

    // Screenshot view
    Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

    PixelCopy.request(surfaceView, bitmap, result -> {
      if (result == PixelCopy.SUCCESS) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        preview = stream.toByteArray();
      }
    }, new Handler(Looper.getMainLooper()));
  }

  private void onHostComplete(String cloudAnchorId, CloudAnchorState cloudState) {
    if (cloudState == CloudAnchorState.SUCCESS) {
      firebaseManager.nextShortCode(shortCode -> {
        if (shortCode != null) {
          String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

          firebaseManager.storeUsingShortCode(shortCode, cloudAnchorId, imageRotation, imageScale, latitude, longitude, title, description, date, getUID());

          StorageReference imageReference = storageReference.child("images/" + shortCode);
          uploadTask = imageReference.putBytes(imageBytes);

          uploadTask.addOnFailureListener(
                  e -> {
                    requireActivity().runOnUiThread(() -> showToast("Upload failed."));
                    uploadButton.setEnabled(true);
                  }
          );

          StorageReference previewReference = storageReference.child("previews/" + shortCode);
          uploadTask = previewReference.putBytes(preview);

          uploadTask.addOnFailureListener(
                  e -> {
                    requireActivity().runOnUiThread(() -> showToast("Upload failed."));
                    uploadButton.setEnabled(true);
                  }
          ).addOnSuccessListener(
                  taskSnapshot -> requireActivity().runOnUiThread(() -> showToast("Upload successful."))
          );
        }
      });
    } else {
      requireActivity().runOnUiThread(() -> showToast("Upload failed."));
      uploadButton.setEnabled(true);
    }
  }

  private String getUID() {
    mAuth = FirebaseAuth.getInstance().getCurrentUser();

    if (mAuth != null) {
      return mAuth.getUid();
    }

    // Set user identifier to 0 for anonymous
    return "0";
  }

  private void onShortCodeEntered(int shortCode) {
    StorageReference imageReference = storageReference.child("images/" + shortCode);
    imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
            bytes -> {
              image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              imageWidth = image.getWidth();
              imageHeight = image.getHeight();
            }
    ).addOnFailureListener(
            e -> requireActivity().runOnUiThread(() -> showToast("Error retrieving image."))
    );

    firebaseManager.getImageRotation(shortCode, rotation -> {
      if (rotation == null) {
        requireActivity().runOnUiThread(() -> showToast("Error retrieving image."));
        return;
      }
      imageRotation = rotation;
    });

    firebaseManager.getImageScale(shortCode, scale -> {
      if (scale == null) {
        requireActivity().runOnUiThread(() -> showToast("Error retrieving image."));
        return;
      }
      imageScale = scale;
    });

    firebaseManager.getCloudAnchorId(shortCode, cloudAnchorId -> {
      if (cloudAnchorId == null || cloudAnchorId.isEmpty()) {
        requireActivity().runOnUiThread(() -> showToast("Error retrieving image."));
        return;
      }
      future = session.resolveCloudAnchorAsync(
          cloudAnchorId, (anchor, cloudState) -> onResolveComplete(anchor, cloudState, shortCode));
    });
  }

  private void onResolveComplete(Anchor anchor, CloudAnchorState cloudState, int shortCode) {
    if (cloudState == CloudAnchorState.SUCCESS) {
      requireActivity().runOnUiThread(() -> showToast("Image successfully loaded."));
      currentAnchor = anchor;

      visualise = false;
      rotationBar.setProgress(180);
      scaleBar.setProgress(100);
    } else {
      requireActivity().runOnUiThread(() -> showToast("Error retrieving image."));
    }
  }

  private final SeekBar.OnSeekBarChangeListener rotationChangeListener = new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      // SeekBar begins at 180 which corresponds to a 0 degree rotation
      // Negative so rotation direction follows SeekBar direction
      imageRotation = -(i - 180) - 135;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
  };

  private final SeekBar.OnSeekBarChangeListener scaleChangeListener = new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      imageScale = i / 100f;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
  };

  private void onShutterButtonPressed() {
    // Screenshot view
    Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

    PixelCopy.request(surfaceView, bitmap, result -> {
      if (result == PixelCopy.SUCCESS) {
        // Save image
        OutputStream imageOutStream;
        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "capture.png");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try {
          imageOutStream = requireContext().getContentResolver().openOutputStream(uri);
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
          imageOutStream.close();

          requireActivity().runOnUiThread(() -> showToast("Image saved."));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, new Handler(Looper.getMainLooper()));
  }

  // Hides previous toast then shows a new one
  private void showToast(String message) {
    if (message.isEmpty()) {
      return;
    }

    if (currentToast != null) {
      currentToast.cancel();
    }

    Activity activity = getActivity();

    if (activity != null) {
      currentToast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
      currentToast.show();
    }
  }
}
