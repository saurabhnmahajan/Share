package com.example.home.share;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean flag = true;
    CameraUpdate zoomLvl;
    DatabaseHandler db = new DatabaseHandler(this);
    String email, selectedContacts;
    ClusterManager<MyLocation> mClusterManager;
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
        mMap.getUiSettings()
                .setMyLocationButtonEnabled(false);
        mMap.getUiSettings()
                .setAllGesturesEnabled(true);
        setUpCluster();
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
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Bundle b = getIntent().getExtras();
        email = b.getString("email");
        selectedContacts =  b.getString("selectedContacts");
        db.updateUserLocation(email, latitude, longitude);
        addItems(email, latitude, longitude);
        final LatLng latLng = new LatLng(latitude, longitude);
        String loc[][] = db.getSelectedContactsLocation(selectedContacts);
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(latLng);
        for(int contact = 0; contact < loc.length; contact++) {
            String tmp_email = loc[contact][0];
            double tmp_lat = Double.parseDouble(loc[contact][1]), tmp_lng = Double.parseDouble(loc[contact][2]);
            addItems(tmp_email, tmp_lat, tmp_lng);
            LatLng tmp = new LatLng(tmp_lat, tmp_lng);
            boundsBuilder.include(tmp);
        }
        LatLngBounds bounds = boundsBuilder.build();
        zoomLvl = CameraUpdateFactory.newLatLngBounds(bounds, 10 , 10, 0);
        if (flag) {
            flag = false;
            mMap.animateCamera(zoomLvl);
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

    private void setUpCluster() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyLocation>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mClusterManager.setRenderer(new ClusterMarker(this, mMap, mClusterManager));
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyLocation>() {
            @Override
            public boolean onClusterClick(Cluster<MyLocation> cluster) {
                return false;
            }
        });
    }

    private void addItems(String email, double lat, double lng) {
        // Add ten cluster items in close proximity, for purposes of this example.
        MyLocation marker = new MyLocation(email, lat, lng);
        mClusterManager.addItem(marker);
    }
}
