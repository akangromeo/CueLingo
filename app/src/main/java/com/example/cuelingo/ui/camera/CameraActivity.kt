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
import android.graphics.RectF
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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cuelingo.R
import com.example.cuelingo.ml.ModelSignLanguage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        get_permission()

//        labels = FileUtil.loadLabels(this, "labels.txt")
        imageProcessor =
            ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = ModelSignLanguage.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        imageView = findViewById(R.id.imageView)

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

                val bitmap = textureView.bitmap

                val byteBuffer = ByteBuffer.allocateDirect(bitmap!!.byteCount)
                bitmap.copyPixelsToBuffer(byteBuffer)
                byteBuffer.rewind()

                val inputFeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 416, 416, 3), DataType.FLOAT32)
                if (byteBuffer.capacity() == inputFeature0.flatSize) {

                    inputFeature0.loadBuffer(byteBuffer)
                } else {
                    // Handle the size mismatch, log an error, or take appropriate action
                    Log.e("TensorFlow", "Size bytebuffer ${byteBuffer}")
                    Log.e("TensorFlow", "Size Tensorbuffer ${inputFeature0.flatSize}")
                }


                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                val canvas = Canvas(bitmap)

                val h = bitmap.height
                val w = bitmap.width
                paint.textSize = h / 15f
                paint.strokeWidth = h / 85f

                outputFeature0.forEachIndexed { x, fl ->
                    val index = x * 4
                    if (fl > 0.5) {
                        paint.setColor(colors.get(index))
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(
                            RectF(
                                outputFeature0.get(index + 1) * w,
                                outputFeature0.get(index) * h,
                                outputFeature0.get(index + 3) * w,
                                outputFeature0.get(index + 2) * h
                            ), paint
                        )
                        paint.style = Paint.Style.FILL
                        canvas.drawText(
                            labels.get(
                                outputFeature0.get(index).toInt()
                            ) + " " + fl.toString(),
                            outputFeature0.get(index + 1) * w,
                            outputFeature0.get(index) * h,
                            paint
                        )
                    }
                }

                imageView.setImageBitmap(bitmap)


            }
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
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
