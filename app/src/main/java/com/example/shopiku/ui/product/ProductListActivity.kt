package com.example.shopiku.ui.product

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopiku.data.repository.ProductRepository
import com.example.shopiku.databinding.ActivityProductListBinding
import com.example.shopiku.ui.common.ProductAdapter
import com.example.shopiku.ui.detail.ProductDetailActivity
import com.example.shopiku.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val productRepository = ProductRepository()
    private lateinit var productAdapter: ProductAdapter

    private var categoryId: String? = null
    private var searchQuery: String? = null
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getStringExtra("CATEGORY_ID")
        searchQuery = intent.getStringExtra("SEARCH_QUERY")

        if (!searchQuery.isNullOrBlank()) {
            binding.etSearch.setText(searchQuery)
        }

        setupUI()
        loadProducts()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        productAdapter = ProductAdapter { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_ID", product.id)
            }
            startActivity(intent)
        }

        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = productAdapter

        // Debounce search listener (300ms) - FR-02 requirement!
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
                    searchQuery = s?.toString()?.trim()
                    loadProducts()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = productRepository.getProducts(searchQuery, categoryId)
            binding.progressBar.visibility = View.GONE
            if (result is Resource.Success) {
                productAdapter.submitList(result.data)
            }
        }
    }
}