package com.brittlepins.brittlepins.ui

import android.content.pm.PackageManager
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brittlepins.brittlepins.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions

private const val CAMERA_PERMISSION_REQUEST_CODE = 10
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

class AddComponentActivity : AppCompatActivity() {
    private val TAG = "AddComponentActivity"
    private lateinit var viewFinder: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_component)

        viewFinder = findViewById(R.id.view_finder)

        configureModel()

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE)
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateTransform() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun configureModel() {
        val conditions = FirebaseModelDownloadConditions.Builder().requireWifi().build()
        val remoteModel = FirebaseRemoteModel.Builder("components")
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build()
        FirebaseModelManager.getInstance().registerRemoteModel(remoteModel);
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            //
        }.build()
        val preview = Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup

            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder().apply { setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY) }.build()
        val imageCapture = ImageCapture(imageCaptureConfig)

        findViewById<FloatingActionButton>(R.id.takePhotoFAB).setOnClickListener {
            imageCapture.takePicture(object : ImageCapture.OnImageCapturedListener() {
                override fun onError(useCaseError: ImageCapture.UseCaseError?, message: String?, cause: Throwable?) {
                    Log.e(TAG, "Could not capture picture")
                }

                override fun onCaptureSuccess(image: ImageProxy?, rotationDegrees: Int) {
                    analyzeImage(image)
                }
            })
        }
        CameraX.bindToLifecycle(this, imageCapture, preview)
    }

    private fun analyzeImage(imageProxy: ImageProxy?) {
        Log.d(TAG, "Image saved and is being analyzed...")
        val img = imageProxy?.image ?: return
        val firebaseImg = FirebaseVisionImage.fromMediaImage(img, FirebaseVisionImageMetadata.ROTATION_0)
        val labelerOptions = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                .setRemoteModelName("components")
                .setConfidenceThreshold(0f)
                .build()
        val labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions)
        labeler.processImage(firebaseImg)
                .addOnSuccessListener { labels -> Log.d(TAG, labels[0].text + ": " + labels[0].confidence) }
                .addOnFailureListener {e -> Log.e(TAG, "Could not label image: " + e.message) }
        imageProxy.close()
    }

    private fun updateTransform() {
        val matrix = Matrix()

        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        viewFinder.setTransform(matrix)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
