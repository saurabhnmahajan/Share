package com.example.home.share;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by home on 18/10/15.
 */
class MyLocation implements ClusterItem {
    private final LatLng mPosition;

    public MyLocation(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
