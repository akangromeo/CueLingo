package com.example.cuelingo.ui.objectdetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cuelingo.R
import com.example.cuelingo.databinding.ActivityCameraBinding


class CameraActivity : AppCompatActivity() {

    private lateinit var activityCameraBinding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(activityCameraBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        activityCameraBinding.navigation.setupWithNavController(navController)

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
