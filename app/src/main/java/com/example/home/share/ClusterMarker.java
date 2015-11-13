package com.example.home.share;

import android.content.Context;
import android.graphics.Color;
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
    String markerColors;
    IconGenerator icons;
    View view;
    int colorCounter = 0, shapeSize;
    int color[] = {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.DKGRAY};
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
        Log.d("sadasd", markerColors);
        BitmapDescriptor markerDescriptor = createCustomMarker(markerText);
        markerOptions.anchor(0.5f, 0.5f).icon(markerDescriptor);
        Marker m = getMarker(marker);
    }

    public BitmapDescriptor createCustomMarker(String markerText) {
        colorCounter = Integer.parseInt(markerColors.charAt(markerColors.indexOf(markerText) + markerText.length()) + "");
        Log.d("aaaa1", markerColors);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(60)  // width in px
                .height(60) // height in px
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(markerText.substring(0, 1), color[colorCounter]);
        icons.setBackground(drawable);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        icons.setContentView(view);
        return (BitmapDescriptorFactory.fromBitmap(icons.makeIcon()));
    }
}