package com.example.home.share;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

class MyLocation implements ClusterItem {
    private final LatLng mPosition;
    private String email, markerColors;
    public MyLocation(String user_email, double lat, double lng, String markerColors) {
        email = user_email;
        mPosition = new LatLng(lat, lng);
        this.markerColors = markerColors;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getEmail() {
        return email;
    }

    public String getMarkerColors() {
        return markerColors;
    }
}
