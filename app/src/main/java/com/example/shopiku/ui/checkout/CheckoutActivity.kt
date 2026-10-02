package com.example.shopiku.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.CartRepository
import com.example.shopiku.data.repository.OrderRepository
import com.example.shopiku.databinding.ActivityCheckoutBinding
import com.example.shopiku.ui.payment.PaymentActivity
import com.example.shopiku.util.CurrencyUtils
import com.example.shopiku.util.Resource
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository

    private var shippingType = "REGULER"
    private var shippingCost = 15000.0
    private var subtotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        cartRepository = CartRepository(sessionManager)
        orderRepository = OrderRepository(sessionManager)

        setupUI()
        loadOrderData()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.rgShipping.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbExpress.id) {
                shippingType = "EXPRESS"
                shippingCost = 25000.0
            } else {
                shippingType = "REGULER"
                shippingCost = 15000.0
            }
            updateCalculations()
        }

        binding.btnPlaceOrder.setOnClickListener {
            lifecycleScope.launch {
                val cartRes = cartRepository.getCartItems()
                if (cartRes is Resource.Success) {
                    val selectedItems = cartRes.data.filter { it.isSelected }
                    if (selectedItems.isEmpty()) {
                        Toast.makeText(this@CheckoutActivity, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val address = binding.tvUserAddress.text.toString()
                    val orderRes = orderRepository.createOrder(selectedItems, address, shippingType)
                    if (orderRes is Resource.Success) {
                        val intent = Intent(this@CheckoutActivity, PaymentActivity::class.java).apply {
                            putExtra("ORDER_ID", orderRes.data.id)
                        }
                        startActivity(intent)
                        finish()
                    } else if (orderRes is Resource.Error) {
                        Toast.makeText(this@CheckoutActivity, orderRes.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadOrderData() {
        lifecycleScope.launch {
            val cartRes = cartRepository.getCartItems()
            if (cartRes is Resource.Success) {
                val items = cartRes.data.filter { it.isSelected }
                subtotal = items.sumOf { it.totalPrice }
                updateCalculations()
            }
        }
    }

    private fun updateCalculations() {
        binding.tvSubtotal.text = CurrencyUtils.formatRupiah(subtotal)
        binding.tvShippingCost.text = CurrencyUtils.formatRupiah(shippingCost)
        binding.tvGrandTotal.text = CurrencyUtils.formatRupiah(subtotal + shippingCost)
    }
}