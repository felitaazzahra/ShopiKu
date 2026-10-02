package com.example.shopiku.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.model.Product
import com.example.shopiku.data.repository.CartRepository
import com.example.shopiku.data.repository.ProductRepository
import com.example.shopiku.databinding.ActivityProductDetailBinding
import com.example.shopiku.ui.checkout.CheckoutActivity
import com.example.shopiku.ui.review.ReviewActivity
import com.example.shopiku.util.CurrencyUtils
import com.example.shopiku.util.Resource
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val productRepository = ProductRepository()
    private lateinit var cartRepository: CartRepository

    private var productId: String = "p1"
    private var currentProduct: Product? = null
    private var selectedVariant: String? = null
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(SessionManager(this))
        productId = intent.getStringExtra("PRODUCT_ID") ?: "p1"

        setupUI()
        loadProductData()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnQtyMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvDetailQty.text = quantity.toString()
            }
        }

        binding.btnQtyPlus.setOnClickListener {
            quantity++
            binding.tvDetailQty.text = quantity.toString()
        }

        binding.btnAddToCart.setOnClickListener {
            if (selectedVariant.isNullOrBlank()) {
                Toast.makeText(this, "Harap pilih varian terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentProduct?.let { prod ->
                lifecycleScope.launch {
                    val res = cartRepository.addToCart(prod, selectedVariant!!, quantity)
                    if (res is Resource.Success) {
                        Toast.makeText(this@ProductDetailActivity, "Berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnBuyNow.setOnClickListener {
            if (selectedVariant.isNullOrBlank()) {
                Toast.makeText(this, "Harap pilih varian terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentProduct?.let { prod ->
                lifecycleScope.launch {
                    cartRepository.addToCart(prod, selectedVariant!!, quantity)
                    val intent = Intent(this@ProductDetailActivity, CheckoutActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.tvSeeAllReviews.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java).apply {
                putExtra("PRODUCT_ID", productId)
            }
            startActivity(intent)
        }
    }

    private fun loadProductData() {
        lifecycleScope.launch {
            val result = productRepository.getProductDetail(productId)
            if (result is Resource.Success) {
                val product = result.data
                currentProduct = product

                binding.tvProductName.text = product.name
                binding.tvPrice.text = CurrencyUtils.formatRupiah(product.price)
                binding.tvRating.text = product.rating.toString()
                binding.tvSoldCount.text = "• ${product.soldCount} Terjual"
                binding.tvDescription.text = product.description

                Glide.with(this@ProductDetailActivity)
                    .load(product.imageUrl)
                    .centerCrop()
                    .into(binding.ivProductMain)

                // Populate Variant Chips
                binding.cgVariants.removeAllViews()
                product.variants.forEach { variant ->
                    val chip = Chip(this@ProductDetailActivity).apply {
                        text = variant.name
                        isCheckable = true
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedVariant = variant.name
                            }
                        }
                    }
                    binding.cgVariants.addView(chip)
                }
            }
        }
    }
}