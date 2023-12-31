package com.example.cuelingo.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cuelingo.data.local.preferences.UserModel
import com.example.cuelingo.data.result.Result
import com.example.cuelingo.databinding.ActivityLoginBinding
import com.example.cuelingo.ui.main.MainActivity
import com.example.cuelingo.ui.register.RegisterActivity
import com.example.cuelingo.ui.viewModelFactory.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val token = UserModel(
                                result.data.loginResult!!.name.toString(),
                                result.data.loginResult.userId.toString(),
                                result.data.loginResult.token!!,
                                true
                            )
                            viewModel.saveSession(token)
                            intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        }

                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                        }

                    }
                }


            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}