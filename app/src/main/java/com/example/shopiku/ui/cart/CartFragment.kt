package com.example.shopiku.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiku.MainActivity
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.model.CartItem
import com.example.shopiku.data.repository.CartRepository
import com.example.shopiku.databinding.FragmentCartBinding
import com.example.shopiku.ui.checkout.CheckoutActivity
import com.example.shopiku.util.CurrencyUtils
import com.example.shopiku.util.Resource
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartRepository: CartRepository
    private lateinit var cartAdapter: CartAdapter
    private var currentItems: List<CartItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartRepository = CartRepository(SessionManager(requireContext()))

        setupAdapter()
        setupListeners()
        loadCartData()
    }

    private fun setupAdapter() {
        cartAdapter = CartAdapter(
            onQuantityChange = { item, newQty ->
                lifecycleScope.launch {
                    val result = cartRepository.updateQuantity(item.id, newQty)
                    if (result is Resource.Success) {
                        currentItems = result.data
                        cartAdapter.submitList(currentItems)
                        updateTotals()
                    }
                }
            },
            onDeleteClick = { item ->
                lifecycleScope.launch {
                    val result = cartRepository.removeCartItem(item.id)
                    if (result is Resource.Success) {
                        currentItems = result.data
                        cartAdapter.submitList(currentItems)
                        updateTotals()
                        Toast.makeText(requireContext(), "Item dihapus dari keranjang", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onSelectToggle = {
                updateTotals()
            }
        )

        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            val selectedItems = currentItems.filter { it.isSelected }
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih minimal 1 produk untuk checkout", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadCartData() {
        lifecycleScope.launch {
            val result = cartRepository.getCartItems()
            if (result is Resource.Success) {
                currentItems = result.data
                cartAdapter.submitList(currentItems)
                updateTotals()
            }
        }
    }

    private fun updateTotals() {
        val selectedItems = currentItems.filter { it.isSelected }
        val total = selectedItems.sumOf { it.totalPrice }
        val count = selectedItems.sumOf { it.quantity }

        binding.tvTotalCartPrice.text = CurrencyUtils.formatRupiah(total)
        binding.btnCheckout.text = "Checkout ($count)"
        binding.btnCheckout.isEnabled = selectedItems.isNotEmpty()

        if (currentItems.isEmpty()) {
            binding.layoutEmptyCart.visibility = View.VISIBLE
            binding.rvCartItems.visibility = View.GONE
            binding.layoutCartBottom.visibility = View.GONE
        } else {
            binding.layoutEmptyCart.visibility = View.GONE
            binding.rvCartItems.visibility = View.VISIBLE
            binding.layoutCartBottom.visibility = View.VISIBLE
        }

        (activity as? MainActivity)?.updateCartBadge()
    }

    override fun onResume() {
        super.onResume()
        loadCartData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}