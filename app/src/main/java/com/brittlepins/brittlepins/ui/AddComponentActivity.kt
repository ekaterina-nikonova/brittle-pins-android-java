package com.brittlepins.brittlepins.ui

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brittlepins.brittlepins.R
import com.brittlepins.brittlepins.helpers.GraphicOverlay
import com.brittlepins.brittlepins.helpers.ObjectConfirmationController
import com.brittlepins.brittlepins.helpers.ObjectGraphicInProminentMode
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions

private const val CAMERA_PERMISSION_REQUEST_CODE = 10
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

class AddComponentActivity : AppCompatActivity() {
    private val TAG = "AddComponentActivity"

    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var preview: Preview
    private lateinit var viewFinder: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_component)

        graphicOverlay = findViewById(R.id.graphicOverlay)
        viewFinder = findViewById(R.id.view_finder)

        graphicOverlay.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

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
        FirebaseModelManager.getInstance().registerRemoteModel(remoteModel)
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            //
        }.build()
        preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup

            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            val analyzerThread = HandlerThread("ObjectDetection").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply { analyzer = ImageAnalyzer(applicationContext, graphicOverlay, preview) }

        CameraX.bindToLifecycle(this, analyzerUseCase, preview)
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

    private class ImageAnalyzer(
            private val ctx: Context,
            private val overlay: GraphicOverlay,
            private val preview: Preview
    ) : ImageAnalysis.Analyzer {
        private val TAG = "ImageAnalyzer"
        private var done = false

        val objectDetectionOptions: FirebaseVisionObjectDetectorOptions = FirebaseVisionObjectDetectorOptions.Builder()
                .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                .build()
        val objectDetector: FirebaseVisionObjectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(objectDetectionOptions)

        override fun analyze(imageProxy: ImageProxy, rotationDegrees: Int) {
            if (!done) {
                val image = imageProxy.image ?: return
                val visionImage = FirebaseVisionImage.fromMediaImage(image, FirebaseVisionImageMetadata.ROTATION_0)

                objectDetector.processImage(visionImage)
                        .addOnSuccessListener { detectedObjects ->
                            if (detectedObjects.size > 0) {
                                done = true
                                showObjectBox(detectedObjects[0])
                                for (obj in detectedObjects) {
                                    labelImage(visionImage, obj.boundingBox)
                                    Log.d(TAG, "${obj.entityId} - ${obj.boundingBox.flattenToString()}")
                                }
                                imageProxy.close()
                            }
                        }
                        .addOnFailureListener { e -> Log.e(TAG, "Could not detect object: ${e.message}") }
            }
        }

        private fun labelImage(image: FirebaseVisionImage, boundingBox: Rect) {
            val labelerOptions = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                    .setRemoteModelName("components")
                    .setConfidenceThreshold(0f)
                    .build()
            val labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions)

            labeler.processImage(FirebaseVisionImage.fromBitmap(image.bitmap))
                    .addOnSuccessListener { labels ->
                        if (labels.size > 0 && labels[0].confidence >= 0.7f) {
                            preview.focus(boundingBox, boundingBox, object: OnFocusListener {
                                override fun onFocusLocked(afRect: Rect?) {
                                    CameraX.unbind(preview)
                                    showNewComponentPrompt(labels[0].text)
                                }

                                override fun onFocusUnableToLock(afRect: Rect?) {
                                    return
                                }

                                override fun onFocusTimedOut(afRect: Rect?) {
                                    return
                                }
                            })
                        } else {
                            done = false
                        }
                    }
                    .addOnFailureListener {
                        e -> Log.e(TAG, "Could not label image: ${e.message}")
                        done = false
                    }
        }

        private fun showNewComponentPrompt(label: String) {
            done = true
            Toast.makeText(ctx, label, Toast.LENGTH_LONG).show()
        }

        private fun showObjectBox(obj: FirebaseVisionObject) {
            overlay.clear()
            overlay.add(ObjectGraphicInProminentMode(overlay, obj, ObjectConfirmationController(overlay)))
        }
    }
}
