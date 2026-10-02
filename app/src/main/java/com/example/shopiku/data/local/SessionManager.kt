package com.example.shopiku.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.shopiku.data.model.CartItem
import com.example.shopiku.data.model.Order
import com.example.shopiku.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "ShopiKu_Session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_CART_ITEMS = "cart_items"
        private const val KEY_ORDERS = "orders"
    }

    fun setLoggedIn(isLoggedIn: Boolean, user: User? = null) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        if (user != null) {
            editor.putString(KEY_USER_DATA, gson.toJson(user))
        } else if (!isLoggedIn) {
            editor.remove(KEY_USER_DATA)
        }
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUser(): User {
        val json = prefs.getString(KEY_USER_DATA, null)
        return if (json != null) {
            try {
                gson.fromJson(json, User::class.java)
            } catch (e: Exception) {
                defaultUser()
            }
        } else {
            defaultUser()
        }
    }

    private fun defaultUser(): User {
        return User(
            id = "user_101",
            name = "Budi Santoso",
            email = "budi.santoso@ikmi.ac.id",
            phone = "081234567890",
            address = "Jl. Kartini No. 45, Kesambi, Kota Cirebon, Jawa Barat (45131)"
        )
    }

    // --- Cart Persistence ---
    fun getCartItems(): List<CartItem> {
        val json = prefs.getString(KEY_CART_ITEMS, null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCartItems(items: List<CartItem>) {
        prefs.edit().putString(KEY_CART_ITEMS, gson.toJson(items)).apply()
    }

    fun addToCart(cartItem: CartItem): List<CartItem> {
        val items = getCartItems().toMutableList()
        val existingIndex = items.indexOfFirst {
            it.product.id == cartItem.product.id && it.variant == cartItem.variant
        }
        if (existingIndex >= 0) {
            items[existingIndex].quantity += cartItem.quantity
        } else {
            items.add(cartItem)
        }
        saveCartItems(items)
        return items
    }

    fun updateCartQuantity(cartItemId: String, newQty: Int): List<CartItem> {
        val items = getCartItems().toMutableList()
        val index = items.indexOfFirst { it.id == cartItemId }
        if (index >= 0) {
            if (newQty <= 0) {
                items.removeAt(index)
            } else {
                items[index].quantity = newQty
            }
            saveCartItems(items)
        }
        return items
    }

    fun removeCartItem(cartItemId: String): List<CartItem> {
        val items = getCartItems().toMutableList()
        items.removeAll { it.id == cartItemId }
        saveCartItems(items)
        return items
    }

    fun clearCart() {
        prefs.edit().remove(KEY_CART_ITEMS).apply()
    }

    // --- Orders Persistence ---
    fun getOrders(): List<Order> {
        val json = prefs.getString(KEY_ORDERS, null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveOrder(order: Order) {
        val orders = getOrders().toMutableList()
        val index = orders.indexOfFirst { it.id == order.id }
        if (index >= 0) {
            orders[index] = order
        } else {
            orders.add(0, order)
        }
        prefs.edit().putString(KEY_ORDERS, gson.toJson(orders)).apply()
    }

    fun updateOrderStatus(orderId: String, status: String) {
        val orders = getOrders().toMutableList()
        val index = orders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            orders[index].status = status
            prefs.edit().putString(KEY_ORDERS, gson.toJson(orders)).apply()
        }
    }
}