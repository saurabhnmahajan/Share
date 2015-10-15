package com.example.home.share;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean flag = true;
    CameraUpdate zoomLvl;
    DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        IconGenerator i = new IconGenerator(getApplicationContext());
        i.setStyle(IconGenerator.STYLE_BLUE);
        // Define the size you want from dimensions file
        int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);

        Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.circle_marker, null);
        i.setBackground(shapeDrawable);

// Create a view container to set the size
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        i.setContentView(view);
        i.setTextAppearance(IconGenerator.STYLE_GREEN);
        final Bitmap bitmap = i.makeIcon();
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double latitude = location.getLatitude();
                // Getting longitude of the current location
                double longitude = location.getLongitude();
                // Creating a LatLng object for the current location
                Bundle b = getIntent().getExtras();
                String user = b.getString("user");
                db.updateUserLocation(user, latitude, longitude);
                final LatLng latLng = new LatLng(latitude, longitude);
                if (flag) {
                    mMap.addMarker(new MarkerOptions().position(latLng)
                            .anchor(0.5f, 0.5f)
//                            .icon(BitmapDescriptorFactory.fromBitmap(R)));
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    boundsBuilder.include(latLng);
                    LatLngBounds bounds = boundsBuilder.build();
                    zoomLvl = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                    mMap.animateCamera(zoomLvl);
                    float zoom = mMap.getCameraPosition().zoom;
                    Toast.makeText(getApplicationContext(), zoom + "", Toast.LENGTH_SHORT).show();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    Toast.makeText(getApplicationContext(), zoom + "", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
        });
    }
}
