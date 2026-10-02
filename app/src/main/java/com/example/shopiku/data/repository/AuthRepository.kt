package com.example.shopiku.data.repository

import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.model.User
import com.example.shopiku.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val sessionManager: SessionManager) {

    suspend fun login(email: String, pass: String): Resource<User> = withContext(Dispatchers.IO) {
        if (email.isBlank() || pass.isBlank()) {
            return@withContext Resource.Error("Email dan kata sandi tidak boleh kosong")
        }
        if (pass.length < 6) {
            return@withContext Resource.Error("Kata sandi minimal 6 karakter")
        }
        val user = User(
            id = "usr_" + System.currentTimeMillis(),
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = email,
            phone = "081234567890",
            address = "Jl. Kartini No. 45, Kesambi, Kota Cirebon, Jawa Barat (45131)"
        )
        sessionManager.setLoggedIn(true, user)
        Resource.Success(user)
    }

    suspend fun register(name: String, email: String, pass: String): Resource<User> = withContext(Dispatchers.IO) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            return@withContext Resource.Error("Harap lengkapi semua kolom pendaftaran")
        }
        if (pass.length < 6) {
            return@withContext Resource.Error("Kata sandi minimal 6 karakter")
        }
        val user = User(
            id = "usr_" + System.currentTimeMillis(),
            name = name,
            email = email,
            phone = "081234567890",
            address = "Jl. Kartini No. 45, Kesambi, Kota Cirebon, Jawa Barat (45131)"
        )
        sessionManager.setLoggedIn(true, user)
        Resource.Success(user)
    }

    fun logout() {
        sessionManager.setLoggedIn(false, null)
    }

    fun getCurrentUser(): User {
        return sessionManager.getUser()
    }

    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
}