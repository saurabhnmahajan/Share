package com.example.home.share;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean flag = true;
    private CameraUpdate zoomLvl;
    private int colorCounter, keyNo = 0, color[] = {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.DKGRAY};
    private DatabaseHandler db = new DatabaseHandler(this);
    private String email, selectedContacts, checkColors, markerColors;
    private ClusterManager<MyLocation> mClusterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle b = getIntent().getExtras();
        email = b.getString("email");
        selectedContacts =  b.getString("selectedContacts");
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
        Log.d("Location Changed", "");
        double latitude, longitude;
        LatLng latLng;
        String loc[][];
        //clearing map contents
        keyNo = 0;
        colorCounter = 0;
        checkColors = null;
        markerColors = null;
        mClusterManager.clearItems();
        mMap.clear();
        if(((LinearLayout)findViewById(R.id.key)).getChildCount() > 0)
                                                                                                                                                                                                                                                        ((LinearLayout)findViewById(R.id.key)).removeAllViews();

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        latLng = new LatLng(latitude, longitude);
        loc = db.getSelectedContactsLocation(selectedContacts);
                                
        addItems(email, latitude, longitude, color[colorGenerator(email)]);
        createKeyPointer(email, latLng);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(latLng);
        for(int contact = 0; contact < loc.length; contact++) {
            String tmp_email = loc[contact][0];
            double tmp_lat = Double.parseDouble(loc[contact][1]), tmp_lng = Double.parseDouble(loc[contact][2]);
            LatLng tmp = new LatLng(tmp_lat, tmp_lng);

            addItems(tmp_email, tmp_lat, tmp_lng, color[colorGenerator(tmp_email)]);
            createKeyPointer(tmp_email, tmp);
            boundsBuilder.include(tmp);
        }
        mClusterManager.cluster();
        LatLngBounds bounds = boundsBuilder.build();
        zoomLvl = CameraUpdateFactory.newLatLngBounds(bounds, 10 , 10, 0);
        if (flag) {
            flag = false;
            mMap.animateCamera(zoomLvl);
        }
        db.updateUserLocation(email, latitude, longitude);
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
        mClusterManager = new ClusterManager<MyLocation>(this, mMap);
        mClusterManager.setRenderer(new ClusterMarker(this, mMap, mClusterManager));
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyLocation>() {
            @Override
            public boolean onClusterClick(Cluster<MyLocation> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                cluster.getPosition(), (float) Math.floor(mMap
                                        .getCameraPosition().zoom + 1)), 300,
                        null);
                return true;
            }
        });
    }

    private void addItems(String email, double lat, double lng, int markerColor) {
        MyLocation marker = new MyLocation(email, lat, lng, markerColor);
        mClusterManager.addItem(marker);
    }

    private void createKeyPointer(String email, final LatLng pos) {
        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams vp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(vp);

        IconGenerator icons = new IconGenerator(getApplicationContext());
        int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);
        int counter = Integer.parseInt(markerColors.charAt(markerColors.indexOf(email) + email.length()) + "");
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(60)
                .height(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(email.substring(0, 1), color[counter]);
        icons.setBackground(drawable);
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        icons.setContentView(view);
        Bitmap bitmap = icons.makeIcon();
        Drawable d = new BitmapDrawable(getResources(), bitmap);

        imageView.setBackground(d);
        LinearLayout mapsView = (LinearLayout)findViewById(R.id.key);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 0, 0, 0);
        imageView.setLayoutParams(layoutParams);
        mapsView.addView(imageView, 0);
        keyNo++;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 20.0f));
            }
        });
    }

    public int colorGenerator(String email) {
        while(checkColors != null && checkColors.contains(email.substring(0, 1) + colorCounter)) {
            colorCounter++;
        }
        if( colorCounter >= color.length) {
            colorCounter = 0;
        }
        checkColors += email.substring(0,1) + colorCounter;
        markerColors += email + colorCounter;
        return colorCounter++;
    }
}
