package com.example.cuelingo.ui.detaildictionary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cuelingo.databinding.ActivityDetailDictionaryBinding
import com.example.cuelingo.ui.dictionary.DictionaryActivity
import com.example.cuelingo.ui.objectdetection.CameraActivity

class DetailDictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDictionaryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        setupAction()


    }

    private fun setData(){

        val name = intent.getStringExtra(NAME)
        val photo = intent.getStringExtra(PHOOTO)

        binding.apply {
            Glide.with(this@DetailDictionaryActivity)
                .load(photo)
                .centerCrop()
                .into(ivDictionary

                )
            tvDictionaryName.text = name
        }

    }

    private fun setupAction(){

        binding.ibTryNow.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.ibFindAnother.setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
        }

    }

    companion object {
        const val NAME = "name"
        const val PHOOTO = "photo"
    }
}