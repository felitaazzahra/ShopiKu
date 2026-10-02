package com.example.shopiku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.databinding.ActivityMainBinding
import com.example.shopiku.ui.cart.CartFragment
import com.example.shopiku.ui.category.CategoryFragment
import com.example.shopiku.ui.home.HomeFragment
import com.example.shopiku.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_category -> loadFragment(CategoryFragment())
                R.id.nav_cart -> loadFragment(CartFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }

        updateCartBadge()
    }

    fun selectBottomTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

    fun updateCartBadge() {
        val count = sessionManager.getCartItems().sumOf { it.quantity }
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_cart)
        if (count > 0) {
            badge.isVisible = true
            badge.number = count
        } else {
            badge.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }
}