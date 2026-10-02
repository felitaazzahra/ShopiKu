package com.example.shopiku.data.repository

import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.model.CartItem
import com.example.shopiku.data.model.Order
import com.example.shopiku.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderRepository(private val sessionManager: SessionManager) {

    suspend fun createOrder(
        items: List<CartItem>,
        address: String,
        shippingType: String // "REGULER" or "EXPRESS"
    ): Resource<Order> = withContext(Dispatchers.IO) {
        if (items.isEmpty()) {
            return@withContext Resource.Error("Keranjang belanja kosong")
        }
        val subtotal = items.sumOf { it.totalPrice }
        val shippingCost = if (shippingType == "EXPRESS") 25000.0 else 15000.0
        val total = subtotal + shippingCost

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val nowStr = dateFormat.format(Date())

        val order = Order(
            id = "ORD-" + System.currentTimeMillis().toString().takeLast(8),
            userId = sessionManager.getUser().id,
            items = items,
            address = address,
            shippingType = shippingType,
            shippingCost = shippingCost,
            subtotal = subtotal,
            total = total,
            status = "PENDING_PAYMENT",
            vaNumber = "123 456 7890",
            createdAt = nowStr
        )

        sessionManager.saveOrder(order)
        sessionManager.clearCart() // Clear cart after order placed
        Resource.Success(order)
    }

    suspend fun simulatePayment(orderId: String): Resource<Order> = withContext(Dispatchers.IO) {
        val orders = sessionManager.getOrders()
        val order = orders.find { it.id == orderId }
            ?: return@withContext Resource.Error("Pesanan tidak ditemukan")

        order.status = "PAID"
        sessionManager.saveOrder(order)
        Resource.Success(order)
    }

    suspend fun getOrders(): Resource<List<Order>> = withContext(Dispatchers.IO) {
        val list = sessionManager.getOrders()
        Resource.Success(list)
    }

    suspend fun getOrderById(orderId: String): Resource<Order> = withContext(Dispatchers.IO) {
        val orders = sessionManager.getOrders()
        val order = orders.find { it.id == orderId }
            ?: return@withContext Resource.Error("Pesanan tidak ditemukan")
        Resource.Success(order)
    }
}