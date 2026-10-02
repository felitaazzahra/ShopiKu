package com.example.shopiku.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.shopiku.MainActivity
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.AuthRepository
import com.example.shopiku.databinding.ActivitySplashBinding
import com.example.shopiku.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        authRepository = AuthRepository(sessionManager)

        Handler(Looper.getMainLooper()).postDelayed({
            if (authRepository.isLoggedIn()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        }, 1500)
    }
}