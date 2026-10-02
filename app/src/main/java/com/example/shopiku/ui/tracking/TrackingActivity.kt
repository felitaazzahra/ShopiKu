package com.example.shopiku.ui.tracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.OrderRepository
import com.example.shopiku.databinding.ActivityTrackingBinding
import com.example.shopiku.util.Resource
import kotlinx.coroutines.launch

class TrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingBinding
    private lateinit var orderRepository: OrderRepository
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderRepository = OrderRepository(SessionManager(this))
        orderId = intent.getStringExtra("ORDER_ID") ?: ""

        binding.btnBack.setOnClickListener { finish() }

        loadTracking()
    }

    private fun loadTracking() {
        if (orderId.isNotBlank()) {
            binding.tvOrderId.text = "No. Pesanan: $orderId"
        }

        lifecycleScope.launch {
            val res = orderRepository.getOrderById(orderId)
            if (res is Resource.Success) {
                val order = res.data
                binding.tvOrderId.text = "No. Pesanan: ${order.id}"
            }
        }
    }
}