package com.google.kpierudzki.driverassistant.history.map.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.common.model.Coordinate;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.util.ScreenUtils;

import java.util.List;

import java8.util.Optional;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 26.11.2017.
 */

public class MapManager {

    private View root;
    private MapView mapView;
    private GoogleMap googleMap;
    private List<HistoryMapContract.MapData> data;
    private Marker marker;

    MapManager(@NonNull View root, @NonNull MapView mapView, @Nullable Bundle savedInstanceState, OnMapReadyCallback callback) {
        this.root = root;
        this.mapView = mapView;
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(callback);
    }

    void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        moveCompassViewToRightSide();
    }

    private void moveCompassViewToRightSide() {
        final String MAP_COMPASS_TAG = "GoogleMapCompass";
        if (mapView != null) {
            View compassView = mapView.findViewWithTag(MAP_COMPASS_TAG);
            if (compassView != null) {
                RelativeLayout.LayoutParams compassParams = new RelativeLayout.LayoutParams(
                        compassView.getWidth(), compassView.getHeight());
                compassParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                compassParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                int compassMargin = ScreenUtils.dpToPx(20);
                compassParams.setMargins(compassMargin, compassMargin, compassMargin, compassMargin);
                compassView.setLayoutParams(compassParams);
            }
        }
    }

    void setData(List<HistoryMapContract.MapData> data) {
        this.data = data;
    }

    List<HistoryMapContract.MapData> getData() {
        return data;
    }

    void drawPolyline() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(StreamSupport.stream(data)
                .map(geoSamplesEntry -> new LatLng(geoSamplesEntry.geoSamplesEntity.getCoordinate().latitude,
                        geoSamplesEntry.geoSamplesEntity.getCoordinate().longitude))
                .toArray(LatLng[]::new));
        Polyline polyline = googleMap.addPolyline(polylineOptions);
        stylePolyline(polyline);

        int middleElement = polyline.getPoints().size() / 2;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(middleElement), 12));
    }

    private void stylePolyline(Polyline polyline) {
        polyline.setWidth(10);
        polyline.setColor(ContextCompat.getColor(root.getContext(), R.color.History_Map_Polyline));
        polyline.setJointType(JointType.ROUND);

        Bitmap startMarker = getBitmapFromVectorDrawable(root.getContext(),
                R.drawable.map_fragment_start_point,
                R.color.History_Map_Polyline_Marker_Start);
        polyline.setStartCap(new CustomCap(BitmapDescriptorFactory.fromBitmap(startMarker), 10));

        Bitmap endMarker = getBitmapFromVectorDrawable(root.getContext(),
                R.drawable.map_fragment_end_point,
                R.color.History_Map_Polyline_Marker_End);
        polyline.setEndCap(new CustomCap(BitmapDescriptorFactory.fromBitmap(endMarker), 10));
    }

    private Bitmap getBitmapFromVectorDrawable(
            Context context,
            @DrawableRes int drawableId,
            @ColorRes int tintColor) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            drawable = (DrawableCompat.wrap(drawable)).mutate();

        drawable.setColorFilter(ContextCompat.getColor(context, tintColor), PorterDuff.Mode.SRC_IN);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    void updateMarker(long offset) {
        Optional<HistoryMapContract.MapData> optional = StreamSupport.parallelStream(data)
                .filter(entity -> entity.geoSamplesEntity.getOffset() == offset)
                .findFirst();
        if (optional.isPresent()) {
            HistoryMapContract.MapData mapData = optional.get();
            Coordinate coordinate = mapData.geoSamplesEntity.getCoordinate();
            LatLng pos = new LatLng(coordinate.latitude, coordinate.longitude);
            if (marker == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                Bitmap icon = getBitmapFromVectorDrawable(root.getContext(),
                        R.drawable.ic_current_location,
                        R.color.History_Map_Polyline_Marker_Current);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                markerOptions.position(pos);
                marker = googleMap.addMarker(markerOptions);
            }
            marker.setPosition(pos);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(coordinate.latitude, coordinate.longitude),
                    googleMap.getCameraPosition().zoom));
        }
    }

    void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    void onResume() {
        mapView.onResume();
    }

    void onPause() {
        mapView.onPause();
    }

    void onStart() {
        mapView.onStart();
    }

    void onStop() {
        mapView.onStop();
    }

    public void onDestroy() {
        mapView.onDestroy();
    }

    void onLowMemory() {
        mapView.onLowMemory();
    }
}
