package com.example.home.share;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean flag = true;
    CameraUpdate zoomLvl;
    DatabaseHandler db = new DatabaseHandler(this);
    Location myLocation;
    LocationManager locationManager;
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
        mMap.setMyLocationEnabled(false);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(bestProvider);
        locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
        if (location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        IconGenerator i = new IconGenerator(getApplicationContext());
        // Define the size you want from dimensions file
        int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);

        Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.circle_marker, null);
        shapeDrawable.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        i.setBackground(shapeDrawable);

        // Create a view container to set the size
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));

        i.setContentView(view);
        final Bitmap bitmap = i.makeIcon();

        double latitude = location.getLatitude();
        // Getting longitude of the current location
        double longitude = location.getLongitude();
        // Creating a LatLng object for the current location
        Bundle b = getIntent().getExtras();
        String user = b.getString("user");
        db.updateUserLocation(user, latitude, longitude);
        final LatLng latLng = new LatLng(latitude, longitude);
        if (flag) {
            flag = false;
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            mMap.getUiSettings()
                    .setMyLocationButtonEnabled(false);
            mMap.getUiSettings()
                    .setAllGesturesEnabled(true);
            String selectedContacts =  b.getString("selectedContacts");
            String loc[][] = db.getSelectedContactsLocation(selectedContacts);
            com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(latLng);
            for(int contact = 0; contact < loc.length; contact++) {
                double tmp_long = Double.parseDouble(loc[contact][2]), tmp_lat = Double.parseDouble(loc[contact][1]);
                LatLng tmp = new LatLng(tmp_lat, tmp_long);
                boundsBuilder.include(tmp);
                mMap.addMarker(new MarkerOptions().position(tmp)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            }
            LatLngBounds bounds = boundsBuilder.build();
            zoomLvl = CameraUpdateFactory.newLatLngBounds(bounds, 10 , 10, 0);
            mMap.animateCamera(zoomLvl);
            float zoom = mMap.getCameraPosition().zoom;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
