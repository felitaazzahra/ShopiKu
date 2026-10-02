package com.example.shopiku.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shopiku.MainActivity
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.AuthRepository
import com.example.shopiku.databinding.ActivityAuthBinding
import com.example.shopiku.util.Resource
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var authRepository: AuthRepository
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(SessionManager(this))

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        if (isLoginMode) {
            binding.tvTitle.text = "Selamat Datang Kembali"
            binding.tvSubtitle.text = "Masuk ke akun ShopiKu Anda"
            binding.tilName.visibility = View.GONE
            binding.btnSubmit.text = "Masuk"
            binding.tvTogglePrompt.text = "Belum punya akun? Daftar"
            binding.btnTabLogin.setTextColor(getColor(com.example.shopiku.R.color.primary))
            binding.btnTabRegister.setTextColor(getColor(com.example.shopiku.R.color.text_secondary))
        } else {
            binding.tvTitle.text = "Buat Akun Baru"
            binding.tvSubtitle.text = "Daftar sekarang untuk mulai belanja"
            binding.tilName.visibility = View.VISIBLE
            binding.btnSubmit.text = "Daftar"
            binding.tvTogglePrompt.text = "Sudah punya akun? Masuk"
            binding.btnTabLogin.setTextColor(getColor(com.example.shopiku.R.color.text_secondary))
            binding.btnTabRegister.setTextColor(getColor(com.example.shopiku.R.color.primary))
        }
    }

    private fun setupListeners() {
        binding.btnTabLogin.setOnClickListener {
            if (!isLoginMode) {
                isLoginMode = true
                setupUI()
            }
        }

        binding.btnTabRegister.setOnClickListener {
            if (isLoginMode) {
                isLoginMode = false
                setupUI()
            }
        }

        binding.tvTogglePrompt.setOnClickListener {
            isLoginMode = !isLoginMode
            setupUI()
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString()?.trim() ?: ""
            val name = binding.etName.text?.toString()?.trim() ?: ""

            lifecycleScope.launch {
                val result = if (isLoginMode) {
                    authRepository.login(email, password)
                } else {
                    authRepository.register(name, email, password)
                }

                when (result) {
                    is Resource.Success -> {
                        Toast.makeText(this@AuthActivity, "Selamat datang, ${result.data.name}!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@AuthActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        binding.btnGoogle.setOnClickListener {
            lifecycleScope.launch {
                authRepository.login("google.user@shopiku.com", "123456")
                Toast.makeText(this@AuthActivity, "Berhasil masuk dengan Google", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}