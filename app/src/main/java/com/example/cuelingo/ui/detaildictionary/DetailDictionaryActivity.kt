package com.example.cuelingo.ui.detaildictionary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cuelingo.R

class DetailDictionaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_dictionary)
    }

    companion object {
        const val ID = "id"
    }
}