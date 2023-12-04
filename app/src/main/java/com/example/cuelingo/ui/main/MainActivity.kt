package com.example.cuelingo.ui.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.cuelingo.R
import com.example.cuelingo.databinding.ActivityMainBinding
import com.example.cuelingo.ui.dictionary.DictionaryActivity
import com.example.cuelingo.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.bottomNavigation.selectedItemId = R.id.home

        setupView()
        setupAction()

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.dictionary -> {
                    startActivity(Intent(this, DictionaryActivity::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false

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

    }
}
