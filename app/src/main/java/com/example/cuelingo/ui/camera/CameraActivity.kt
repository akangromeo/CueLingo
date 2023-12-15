package com.example.cuelingo.ui.camera

//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cuelingo.R
import com.example.cuelingo.ml.ModelSignLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {


    lateinit var labels: List<String>
    var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    )
    val paint = Paint()
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    lateinit var imageView: ImageView
    lateinit var cameraDevice: CameraDevice
    lateinit var handler: Handler
    lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var model: ModelSignLanguage
    lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        get_permission()

//        labels = FileUtil.loadLabels(this, "labels.txt")
        labels = listOf("text")
        imageProcessor =
            ImageProcessor.Builder().add(ResizeOp(416, 416, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = ModelSignLanguage.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        imageView = findViewById(R.id.imageView)
        imageView.visibility = View.GONE

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                GlobalScope.launch(Dispatchers.IO) {
                    processImage()
                }
            }
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private suspend fun processImage() = withContext(Dispatchers.Main) {
        val bitmap = textureView.bitmap

        val byteBuffer = ByteBuffer.allocateDirect(bitmap!!.byteCount)
        bitmap.copyPixelsToBuffer(byteBuffer)
        byteBuffer.rewind()

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 416, 416, 3), DataType.FLOAT32)

        if (byteBuffer.capacity() == inputFeature0.flatSize) {
            inputFeature0.loadBuffer(byteBuffer)
        } else {
            Log.e("TensorFlow", "Size mismatch: Bytebuffer size ${byteBuffer.capacity()}, Tensorbuffer size ${inputFeature0.flatSize}")
            return@withContext
        }

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        val h = resultBitmap.height
        val w = resultBitmap.width
        paint.textSize = h / 15f
        paint.strokeWidth = h / 85f

        val confidenceThreshold = 0.5

        for (index in 0 until outputFeature0.size / 5) {
            val startIndex = index * 5
            if (startIndex + 4 < outputFeature0.size) {
                val confidence = outputFeature0[startIndex + 4]

                Log.d("detect", "detect: $index, confidence: $confidence")

                if (confidence > confidenceThreshold) {
                    val left = outputFeature0[startIndex + 1] * w
                    val top = outputFeature0[startIndex] * h
                    val right = outputFeature0[startIndex + 3] * w
                    val bottom = outputFeature0[startIndex + 2] * h

                    paint.color = colors[index % colors.size]
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2f

                    canvas.drawRect(left, top, right, bottom, paint)

                    paint.style = Paint.Style.FILL
                    paint.textSize = 20f
                    canvas.drawText(
                        "${labels[index]} $confidence",
                        left,
                        top - 10f,
                        paint
                    )
                }
            } else {
                Log.e("detect", "Index out of bounds: $index")
            }

        }

        imageView.setImageBitmap(resultBitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
        cameraExecutor.shutdown()
    }

    @SuppressLint("MissingPermission")
    fun open_camera() {
        cameraManager.openCamera(
            cameraManager.cameraIdList[0],
            object : CameraDevice.StateCallback() {
                override fun onOpened(p0: CameraDevice) {
                    cameraDevice = p0

                    var surfaceTexture = textureView.surfaceTexture
                    var surface = Surface(surfaceTexture)

                    var captureRequest =
                        cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    captureRequest.addTarget(surface)

                    cameraDevice.createCaptureSession(
                        listOf(surface),
                        object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(p0: CameraCaptureSession) {
                                p0.setRepeatingRequest(captureRequest.build(), null, null)
                            }

                            override fun onConfigureFailed(p0: CameraCaptureSession) {
                            }
                        },
                        handler
                    )
                }

                override fun onDisconnected(p0: CameraDevice) {

                }

                override fun onError(p0: CameraDevice, p1: Int) {

                }
            },
            handler
        )
    }

    fun get_permission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            get_permission()
        }
    }
}
