package com.example.cuelingo.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuelingo.data.local.database.dummyData.generateDummyData
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
        setupAction()
        setData()


    }

    private fun setData() {

        val layoutManager = LinearLayoutManager(this)
        binding.rvRankWord.layoutManager = layoutManager

        val dummyData = generateDummyData()
        val adapter = RankAdapter(dummyData)

        binding.rvRankWord.adapter = adapter
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                binding.tvIntro1.text = "Hi, " + user.name + "!"
            }
        }

    }

    private fun setupAction() {

        binding.ibCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.ibDictionary.setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            recreate()
        }

    }
}
