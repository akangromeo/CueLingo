package com.example.cuelingo.ui.register
import com.example.cuelingo.data.result.Result
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cuelingo.databinding.ActivityRegisterBinding
import com.example.cuelingo.ui.ViewModelFactory
import com.example.cuelingo.ui.login.LoginActivity
import com.example.cuelingo.ui.login.LoginViewModel

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        setupAction()
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

        binding.btnRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.register(name, email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
//                        showLoading(true)
                    }

                    is Result.Success -> {
//                        showLoading(false)
                        Toast.makeText(
                            this,
                            "Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }

                    is Result.Error -> {
//                        showLoading(false)
                        Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}