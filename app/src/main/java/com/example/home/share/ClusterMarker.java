package com.example.home.share;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

class ClusterMarker extends DefaultClusterRenderer<MyLocation> {
    private int markerColors, shapeSize;
    private IconGenerator icons;
    private View view;
    public ClusterMarker(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
        icons = new IconGenerator(context);
        view = new View(context);
        shapeSize = context.getResources().getDimensionPixelSize(R.dimen.shape_size);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        //start clustering if at least 2 items overlap
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyLocation marker, MarkerOptions markerOptions) {
        String markerText = marker.getEmail();
        markerColors = marker.getMarkerColors();
        BitmapDescriptor markerDescriptor = createCustomMarker(markerText);
        markerOptions.anchor(0.5f, 0.5f).icon(markerDescriptor);
        Marker m = getMarker(marker);
        setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyLocation>() {
            @Override
            public boolean onClusterItemClick(MyLocation myLocation) {
                Log.d("cluster item clicked",myLocation.getEmail());
                return false;
            }
        });

    }


    public BitmapDescriptor createCustomMarker(String markerText) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(60)  // width in px
                .height(60) // height in px
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(markerText.substring(0, 1), markerColors);
        icons.setBackground(drawable);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        icons.setContentView(view);
        return (BitmapDescriptorFactory.fromBitmap(icons.makeIcon()));
    }
}