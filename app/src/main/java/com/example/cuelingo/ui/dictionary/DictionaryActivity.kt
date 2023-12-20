package com.example.cuelingo.ui.dictionary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuelingo.R
import com.example.cuelingo.data.remote.response.ListDictionaryItem
import com.example.cuelingo.data.result.Result
import com.example.cuelingo.databinding.ActivityDictionaryBinding
import com.example.cuelingo.ui.detaildictionary.DetailDictionaryActivity
import com.example.cuelingo.ui.main.MainActivity
import com.example.cuelingo.ui.viewModelFactory.ViewModelFactoryDictionary
import com.google.android.material.bottomnavigation.BottomNavigationView

class DictionaryActivity : AppCompatActivity() {

    private val viewModel by viewModels<DictionaryViewModel> {
        ViewModelFactoryDictionary.getInstance(this)
    }

//    private val mainViewModel by viewModels<MainViewModel> {
//        ViewModelFactory.getInstance(this)
//    }

    private lateinit var binding: ActivityDictionaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvDictionary.layoutManager = layoutManager

        setDictionaryData()

        binding.bottomNavigation.selectedItemId = R.id.dictionary

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation

        setupView()



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
//                    mainViewModel.logout()
                    startActivity(Intent(this,MainActivity::class.java) )
                    true
                }
                else -> false

            }
        }



    }

    private fun setDictionaryData(){
        viewModel.getAllDictionary().observe(this){item ->
            if (item != null) {
                when (item) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val dictionary = item.data
                        val dictionaryAdapter =
                            DictionaryAdapter(object : DictionaryAdapter.OnItemClickCallBack {
                                override fun onItemClicked(data: ListDictionaryItem) {
                                    val intent = Intent(
                                        this@DictionaryActivity, DetailDictionaryActivity::class.java
                                    )
                                    intent.putExtra(DetailDictionaryActivity.NAME, data.name)
                                    intent.putExtra(DetailDictionaryActivity.PHOOTO, data.id)
                                    startActivity(intent)
                                }
                            })
                        dictionaryAdapter.submitList(dictionary)
                        binding.rvDictionary.adapter = dictionaryAdapter
                    }

                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Failed to Load", Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Log.d("data", "data empty")
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}