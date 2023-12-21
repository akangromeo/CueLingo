package com.example.cuelingo.ui.objectdetection.fragments

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cuelingo.databinding.FragmentGalleryBinding
import com.example.cuelingo.ui.objectdetection.CameraViewModel
import com.example.cuelingo.ui.objectdetection.ObjectDetectorHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class GalleryFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    enum class MediaType {
        IMAGE, VIDEO, UNKNOWN
    }

    private var _fragmentGalleryBinding: FragmentGalleryBinding? = null
    private val fragmentGalleryBinding
        get() = _fragmentGalleryBinding!!
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    private lateinit var backgroundExecutor: ScheduledExecutorService
    private val viewModel: CameraViewModel by activityViewModels()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { mediaUri ->
                when (val mediaType = loadMediaType(mediaUri)) {
                    MediaType.IMAGE -> runDetectionOnImage(mediaUri)
                    MediaType.VIDEO -> runDetectionOnVideo(mediaUri)
                    MediaType.UNKNOWN -> {
                        updateDisplayView(mediaType)
                        Toast.makeText(
                            requireContext(), "Unsupported data type.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _fragmentGalleryBinding = FragmentGalleryBinding.inflate(inflater, container, false)

        return fragmentGalleryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentGalleryBinding.fabGetContent.setOnClickListener {
            getContent.launch(arrayOf("image/*", "video/*"))
        }

        initBottomSheetControls()
    }

    override fun onPause() {
        fragmentGalleryBinding.overlay.clear()
        if (fragmentGalleryBinding.videoView.isPlaying) {
            fragmentGalleryBinding.videoView.stopPlayback()
        }
        fragmentGalleryBinding.videoView.visibility = View.GONE
        super.onPause()
    }

    private fun initBottomSheetControls() {
        updateControlsUi()

        fragmentGalleryBinding.bottomSheetLayout.thresholdMinus.setOnClickListener {
            if (viewModel.currentThreshold >= 0.1) {
                viewModel.setThreshold(viewModel.currentThreshold - 0.1f)
                updateControlsUi()
            }
        }

        fragmentGalleryBinding.bottomSheetLayout.thresholdPlus.setOnClickListener {
            if (viewModel.currentThreshold <= 0.8) {
                viewModel.setThreshold(viewModel.currentThreshold + 0.1f)
                updateControlsUi()
            }
        }

        fragmentGalleryBinding.bottomSheetLayout.maxResultsMinus.setOnClickListener {
            if (viewModel.currentMaxResults > 1) {
                viewModel.setMaxResults(viewModel.currentMaxResults - 1)
                updateControlsUi()
            }
        }

        fragmentGalleryBinding.bottomSheetLayout.maxResultsPlus.setOnClickListener {
            if (viewModel.currentMaxResults < 5) {
                viewModel.setMaxResults(viewModel.currentMaxResults + 1)
                updateControlsUi()
            }
        }

        fragmentGalleryBinding.bottomSheetLayout.spinnerDelegate.setSelection(
            viewModel.currentDelegate, false
        )
        fragmentGalleryBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                ) {

                    viewModel.setDelegate(p2)
                    updateControlsUi()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {/* no op */
                }
            }

        fragmentGalleryBinding.bottomSheetLayout.spinnerModel.setSelection(
            viewModel.currentModel, false
        )
        fragmentGalleryBinding.bottomSheetLayout.spinnerModel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                ) {
                    viewModel.setModel(p2)
                    updateControlsUi()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun updateControlsUi() {
        if (fragmentGalleryBinding.videoView.isPlaying) {
            fragmentGalleryBinding.videoView.stopPlayback()
        }
        fragmentGalleryBinding.videoView.visibility = View.GONE
        fragmentGalleryBinding.imageResult.visibility = View.GONE
        fragmentGalleryBinding.overlay.clear()
        fragmentGalleryBinding.bottomSheetLayout.maxResultsValue.text =
            viewModel.currentMaxResults.toString()
        fragmentGalleryBinding.bottomSheetLayout.thresholdValue.text =
            String.format("%.2f", viewModel.currentThreshold)

        fragmentGalleryBinding.overlay.clear()
        fragmentGalleryBinding.tvPlaceholder.visibility = View.VISIBLE
    }

    private fun runDetectionOnImage(uri: Uri) {
        fragmentGalleryBinding.overlay.setRunningMode(RunningMode.IMAGE)
        setUiEnabled(false)
        backgroundExecutor = Executors.newSingleThreadScheduledExecutor()
        updateDisplayView(MediaType.IMAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                requireActivity().contentResolver, uri
            )
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver, uri
            )
        }.copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
            fragmentGalleryBinding.imageResult.setImageBitmap(bitmap)

            // Run object detection on the input image
            backgroundExecutor.execute {

                objectDetectorHelper = ObjectDetectorHelper(
                    context = requireContext(),
                    threshold = viewModel.currentThreshold,
                    currentDelegate = viewModel.currentDelegate,
                    currentModel = viewModel.currentModel,
                    maxResults = viewModel.currentMaxResults,
                    runningMode = RunningMode.IMAGE,
                    objectDetectorListener = this
                )

                objectDetectorHelper.detectImage(bitmap)?.let { resultBundle ->
                    activity?.runOnUiThread {
                        fragmentGalleryBinding.overlay.setResults(
                            resultBundle.results[0],
                            bitmap.height,
                            bitmap.width,
                            resultBundle.inputImageRotation
                        )

                        setUiEnabled(true)
                        fragmentGalleryBinding.bottomSheetLayout.inferenceTimeVal.text =
                            String.format(
                                "%d ms", resultBundle.inferenceTime
                            )
                    }
                } ?: run {
                    Log.e(TAG, "Error running object detection.")
                }

                objectDetectorHelper.clearObjectDetector()
            }
        }
    }

    private fun runDetectionOnVideo(uri: Uri) {
        fragmentGalleryBinding.overlay.setRunningMode(RunningMode.VIDEO)
        setUiEnabled(false)
        updateDisplayView(MediaType.VIDEO)

        with(fragmentGalleryBinding.videoView) {
            setVideoURI(uri)
            // mute the audio
            setOnPreparedListener { it.setVolume(0f, 0f) }
            requestFocus()
        }

        backgroundExecutor = Executors.newSingleThreadScheduledExecutor()
        backgroundExecutor.execute {

            objectDetectorHelper = ObjectDetectorHelper(
                context = requireContext(),
                threshold = viewModel.currentThreshold,
                currentDelegate = viewModel.currentDelegate,
                currentModel = viewModel.currentModel,
                maxResults = viewModel.currentMaxResults,
                runningMode = RunningMode.VIDEO,
                objectDetectorListener = this
            )

            activity?.runOnUiThread {
                fragmentGalleryBinding.videoView.visibility = View.GONE
                fragmentGalleryBinding.progress.visibility = View.VISIBLE
            }

            objectDetectorHelper.detectVideoFile(uri, VIDEO_INTERVAL_MS)?.let { resultBundle ->
                activity?.runOnUiThread { displayVideoResult(resultBundle) }
            } ?: run {
                activity?.runOnUiThread {
                    fragmentGalleryBinding.progress.visibility = View.GONE
                }
                Log.e(TAG, "Error running object detection.")
            }

            objectDetectorHelper.clearObjectDetector()
        }
    }

    private fun displayVideoResult(result: ObjectDetectorHelper.ResultBundle) {

        fragmentGalleryBinding.videoView.visibility = View.VISIBLE
        fragmentGalleryBinding.progress.visibility = View.GONE

        fragmentGalleryBinding.videoView.start()
        val videoStartTimeMs = SystemClock.uptimeMillis()

        backgroundExecutor.scheduleAtFixedRate(
            {
                activity?.runOnUiThread {
                    val videoElapsedTimeMs = SystemClock.uptimeMillis() - videoStartTimeMs
                    val resultIndex = videoElapsedTimeMs.div(VIDEO_INTERVAL_MS).toInt()

                    if (resultIndex >= result.results.size || fragmentGalleryBinding.videoView.visibility == View.GONE) {
                        backgroundExecutor.shutdown()
                    } else {
                        fragmentGalleryBinding.overlay.setResults(
                            result.results[resultIndex],
                            result.inputImageHeight,
                            result.inputImageWidth,
                            result.inputImageRotation
                        )

                        setUiEnabled(true)

                        fragmentGalleryBinding.bottomSheetLayout.inferenceTimeVal.text =
                            String.format("%d ms", result.inferenceTime)
                    }
                }
            }, 0, VIDEO_INTERVAL_MS, TimeUnit.MILLISECONDS
        )
    }

    private fun updateDisplayView(mediaType: MediaType) {
        fragmentGalleryBinding.overlay.clear()
        fragmentGalleryBinding.imageResult.visibility =
            if (mediaType == MediaType.IMAGE) View.VISIBLE else View.GONE
        fragmentGalleryBinding.videoView.visibility =
            if (mediaType == MediaType.VIDEO) View.VISIBLE else View.GONE
        fragmentGalleryBinding.tvPlaceholder.visibility =
            if (mediaType == MediaType.UNKNOWN) View.VISIBLE else View.GONE
    }

    private fun loadMediaType(uri: Uri): MediaType {
        val mimeType = context?.contentResolver?.getType(uri)
        mimeType?.let {
            if (mimeType.startsWith("image")) return MediaType.IMAGE
            if (mimeType.startsWith("video")) return MediaType.VIDEO
        }

        return MediaType.UNKNOWN
    }

    private fun setUiEnabled(enabled: Boolean) {
        fragmentGalleryBinding.fabGetContent.isEnabled = enabled
        fragmentGalleryBinding.bottomSheetLayout.spinnerModel.isEnabled = enabled
        fragmentGalleryBinding.bottomSheetLayout.thresholdMinus.isEnabled = enabled
        fragmentGalleryBinding.bottomSheetLayout.thresholdPlus.isEnabled = enabled
        fragmentGalleryBinding.bottomSheetLayout.maxResultsMinus.isEnabled = enabled
        fragmentGalleryBinding.bottomSheetLayout.maxResultsPlus.isEnabled = enabled
        fragmentGalleryBinding.bottomSheetLayout.spinnerDelegate.isEnabled = enabled
    }

    private fun detectError() {
        activity?.runOnUiThread {
            fragmentGalleryBinding.progress.visibility = View.GONE
            setUiEnabled(true)
            updateDisplayView(MediaType.UNKNOWN)
        }
    }

    override fun onError(error: String, errorCode: Int) {
        detectError()
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            if (errorCode == ObjectDetectorHelper.GPU_ERROR) {
                fragmentGalleryBinding.bottomSheetLayout.spinnerDelegate.setSelection(
                    ObjectDetectorHelper.DELEGATE_CPU, false
                )
            }
        }
    }

    override fun onResults(resultBundle: ObjectDetectorHelper.ResultBundle) {
        // no-op
    }

    companion object {
        private const val TAG = "GalleryFragment"

        private const val VIDEO_INTERVAL_MS = 300L
    }
}
