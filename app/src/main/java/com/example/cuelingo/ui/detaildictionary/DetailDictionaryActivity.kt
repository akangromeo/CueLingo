package com.example.cuelingo.ui.detaildictionary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cuelingo.databinding.ActivityDetailDictionaryBinding

class DetailDictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDictionaryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()


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

    companion object {
        const val NAME = "name"
        const val PHOOTO = "photo"
    }
}