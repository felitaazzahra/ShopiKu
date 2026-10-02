package com.example.shopiku.data.repository

import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.model.CartItem
import com.example.shopiku.data.model.Product
import com.example.shopiku.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val sessionManager: SessionManager) {

    suspend fun getCartItems(): Resource<List<CartItem>> = withContext(Dispatchers.IO) {
        val items = sessionManager.getCartItems()
        Resource.Success(items)
    }

    suspend fun addToCart(
        product: Product,
        variantName: String,
        quantity: Int = 1
    ): Resource<List<CartItem>> = withContext(Dispatchers.IO) {
        if (variantName.isBlank()) {
            return@withContext Resource.Error("Harap pilih varian produk terlebih dahulu")
        }
        val cartItem = CartItem(
            id = "cart_" + System.currentTimeMillis(),
            product = product,
            variant = variantName,
            quantity = quantity
        )
        val updatedList = sessionManager.addToCart(cartItem)
        Resource.Success(updatedList)
    }

    suspend fun updateQuantity(cartItemId: String, newQty: Int): Resource<List<CartItem>> = withContext(Dispatchers.IO) {
        val updatedList = sessionManager.updateCartQuantity(cartItemId, newQty)
        Resource.Success(updatedList)
    }

    suspend fun removeCartItem(cartItemId: String): Resource<List<CartItem>> = withContext(Dispatchers.IO) {
        val updatedList = sessionManager.removeCartItem(cartItemId)
        Resource.Success(updatedList)
    }

    fun clearCart() {
        sessionManager.clearCart()
    }
}