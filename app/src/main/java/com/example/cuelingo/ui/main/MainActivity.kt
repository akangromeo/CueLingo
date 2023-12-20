package com.example.cuelingo.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cuelingo.databinding.ActivityMainBinding
import com.example.cuelingo.ui.dictionary.DictionaryActivity
import com.example.cuelingo.ui.login.LoginActivity
import com.example.cuelingo.ui.objectdetection.CameraActivity
import com.example.cuelingo.ui.viewModelFactory.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getSession()
        setupView()
        setupAction()


    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {

            }
        }

    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {

        binding.ibCamera.setOnClickListener{
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.ibDictionary.setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
        }

    }
}
