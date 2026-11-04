package com.itismob.s03.wonder.pets.animospaces.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.itismob.s03.wonder.pets.animospaces.model.Building;
import com.itismob.s03.wonder.pets.animospaces.model.Campus;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapboxManager {

    private static final String TAG = "MapboxManager";

    public interface OnBuildingClickListener {
        void onBuildingClick(Building building);
    }

    private final MapView mapView;
    private final Context context;
    private PointAnnotationManager pointAnnotationManager;
    private OnBuildingClickListener buildingClickListener;
    private final Map<String, Building> annotationBuildingMap = new HashMap<>();

    public MapboxManager(@NonNull MapView mapView, @NonNull Context context) {
        this.mapView = mapView;
        this.context = context;
        initMap();
    }

    private void initMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            Log.d(TAG, "Mapbox style loaded successfully");
            setupAnnotationManager();
        });
    }

    private void setupAnnotationManager() {
        AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
        if (annotationPlugin != null) {
            pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, new AnnotationConfig());
            pointAnnotationManager.addClickListener(annotation -> {
                String annotationId = annotation.getId();
                Building b = annotationBuildingMap.get(annotationId);
                if (b != null && buildingClickListener != null) {
                    buildingClickListener.onBuildingClick(b);
                    return true;
                }
                return false;
            });
        }
    }

    public void setOnBuildingClickListener(OnBuildingClickListener listener) {
        this.buildingClickListener = listener;
    }

    public void moveCameraToCampus(@NonNull Campus campus) {
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(campus.getLongitude(), campus.getLatitude()))
                .zoom(campus.getDefaultZoom())
                .pitch(35.0)
                .bearing(0.0)
                .build();

        CameraAnimationsUtils.flyTo(
                mapView.getMapboxMap(),
                cameraOptions,
                new MapAnimationOptions.Builder().duration(1800L).build(),
                null
        );
    }

    public void moveCameraToBuilding(@NonNull Building building) {
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(building.getLongitude(), building.getLatitude()))
                .zoom(19.2)
                .pitch(45.0)
                .bearing(10.0)
                .build();

        CameraAnimationsUtils.flyTo(
                mapView.getMapboxMap(),
                cameraOptions,
                new MapAnimationOptions.Builder().duration(1400L).build(),
                null
        );
    }

    public void displayBuildingMarkers(@NonNull List<Building> buildings) {
        if (pointAnnotationManager == null) {
            setupAnnotationManager();
        }
        if (pointAnnotationManager == null) return;

        pointAnnotationManager.deleteAll();
        annotationBuildingMap.clear();

        for (Building building : buildings) {
            Bitmap badgeBitmap = createBuildingBadgeBitmap(building.getCode());
            PointAnnotationOptions options = new PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(building.getLongitude(), building.getLatitude()))
                    .withIconImage(badgeBitmap)
                    .withIconAnchor(IconAnchor.BOTTOM)
                    .withIconSize(1.0f);

            PointAnnotation annotation = pointAnnotationManager.create(options);
            annotationBuildingMap.put(annotation.getId(), building);
        }
    }

    /**
     * Renders a high-resolution green & white DLSU building pin badge.
     * The pin includes a pointer stem at the bottom center to accurately point at the coordinates.
     */
    private Bitmap createBuildingBadgeBitmap(String text) {
        // High resolution for sharp rendering on retina / high-DPI screens
        int width = 220;
        int badgeHeight = 76;
        int stemHeight = 24;
        int totalHeight = badgeHeight + stemHeight;

        Bitmap bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // DLSU Green background paint
        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.parseColor("#005A36")); // DLSU Green
        bgPaint.setStyle(Paint.Style.FILL);

        // Draw rounded rectangle badge body
        RectF rectF = new RectF(6, 6, width - 6, badgeHeight);
        canvas.drawRoundRect(rectF, 20, 20, bgPaint);

        // Draw pin pointer stem at bottom center pointing directly to the building location
        Path stemPath = new Path();
        float centerX = width / 2f;
        stemPath.moveTo(centerX - 18, badgeHeight - 2);
        stemPath.lineTo(centerX + 18, badgeHeight - 2);
        stemPath.lineTo(centerX, totalHeight - 2);
        stemPath.close();
        canvas.drawPath(stemPath, bgPaint);

        // Draw crisp white outline border around badge and stem
        Paint strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5f);
        canvas.drawRoundRect(rectF, 20, 20, strokePaint);

        // Draw white text
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30.0f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        float x = width / 2f;
        float y = (badgeHeight / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);
        canvas.drawText(text, x, y, textPaint);

        return bitmap;
    }
}