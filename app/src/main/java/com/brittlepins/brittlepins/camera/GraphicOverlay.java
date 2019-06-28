package com.brittlepins.brittlepins.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

public class GraphicOverlay extends View {
    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clear() {}

    public void setCameraInfo(CameraSource cameraSource) {}
}
