package org.kisio.labs.hellohere;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.View;

public class BasicMapActivity extends AppCompatActivity {
    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    // Initial map scheme, initialized in onCreate() and accessed in goHome()
    private static String initial_scheme = "";

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        System.out.println("checkPermissions() : BEGIN");

        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            System.out.println("checkPermissions() : START " + permission);

            final int result = ContextCompat.checkSelfPermission(this, permission);
            System.out.println("checkPermissions() : checking " + permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                System.out.println("checkPermissions() : will ask " + permission);
                missingPermissions.add(permission);
            }
            System.out.println("checkPermissions() : STOP " + permission);
        }

        System.out.println("checkPermissions() : missingPermissions check");
        if (!missingPermissions.isEmpty()) {
            System.out.println("checkPermissions() : missingPermissions not empty");

            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            for (String permission : permissions) {
                System.out.println("checkPermissions() : missingPermissions => " + permission);
            }
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            System.out.println("checkPermissions() : missingPermissions empty");
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
        System.out.println("checkPermissions() : END");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Required permission '" + permissions[index]
                                + "' not granted");

                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    // Functionality for taps of the "Go Home" button
    public void goHome(View view) {
        if (map != null) {
            // Change map view to "home" coordinate and zoom level
            map.setCenter(new GeoCoordinate(48.8000824,2.4411199, 0.0),
                    Map.Animation.NONE);
            map.setZoomLevel(map.getMaxZoomLevel());
        }

        // Reset the map scheme to the initial scheme
        if (!initial_scheme.isEmpty()) {
            map.setMapScheme(initial_scheme);
        }
    }

    private void onMapFragmentInitializationCompleted() {
        // retrieve a reference of the map from the map fragment
        map = mapFragment.getMap();
        // Set the map center coordinate to the Vancouver region (no animation)
        map.setCenter(new GeoCoordinate(48.8000824,2.4411199, 0.0),
                Map.Animation.NONE);
        // Set the map zoom level to the average between min and max (no
        // animation)
        map.setZoomLevel(map.getMaxZoomLevel());
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(
                R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error)
            {
                if (error == OnEngineInitListener.Error.NONE) {
                    onMapFragmentInitializationCompleted();
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }

    // Functionality for taps of the "Change Map Scheme" button
    public void changeScheme(View view) {
        if (map != null) {
            // Local variable representing the current map scheme
            String current = map.getMapScheme();
            // Local array containing string values of available map schemes
            List<String> schemes = map.getMapSchemes();
            // Local variable representing the number of available map schemes
            int total = map.getMapSchemes().size();

            if (initial_scheme.isEmpty())
            {
                //save the initial scheme
                initial_scheme = current;
            }

            // If the current scheme is the last element in the array, reset to
            // the scheme at array index 0
            if (schemes.get(total - 1).equals(current))
                map.setMapScheme(schemes.get(0));
            else {
                // If the current scheme is any other element, set to the next
                // scheme in the array
                for (int count = 0; count < total - 1; count++) {
                    if (schemes.get(count).equals(current))
                        map.setMapScheme(schemes.get(count + 1));
                }
            }
        }
    }
}
