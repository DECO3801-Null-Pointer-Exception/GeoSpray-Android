package au.edu.uq.deco3801.nullpointerexception.geospray.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Helper to ask for location permissions.
 * <br/>
 * Code adapted from Google's Cloud Anchors Codelab, available at:
 * <a href="https://github.com/google-ar/codelab-cloud-anchors">
 *     https://github.com/google-ar/codelab-cloud-anchors</a>
 * <br/>
 * Modified by: Raymond Dufty.
 * <br/>
 * Modifications include: changing from camera permission to location permission.
 */
public class LocationPermissionHelper {
    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static boolean hasLocationPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, FINE_LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {FINE_LOCATION_PERMISSION}, LOCATION_PERMISSION_CODE
        );
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE_LOCATION_PERMISSION);
    }

    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
}
