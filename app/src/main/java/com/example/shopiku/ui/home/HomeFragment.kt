package com.example.shopiku.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiku.MainActivity
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.ProductRepository
import com.example.shopiku.databinding.FragmentHomeBinding
import com.example.shopiku.ui.common.CategoryAdapter
import com.example.shopiku.ui.common.ProductAdapter
import com.example.shopiku.ui.detail.ProductDetailActivity
import com.example.shopiku.ui.product.ProductListActivity
import com.example.shopiku.util.Resource
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productRepository = ProductRepository()
    private lateinit var sessionManager: SessionManager

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var flashSaleAdapter: ProductAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, layoutInstanceState: Bundle?) {
        super.onViewCreated(view, layoutInstanceState)
        sessionManager = SessionManager(requireContext())

        setupBanners()
        setupAdapters()
        setupListeners()
        loadHomeData()
        updateCartBadge()
    }

    private fun setupBanners() {
        val banners = listOf(
            BannerItem("Mega Streetwear Cirebon", "Diskon Hingga 50% Seri Sneakers", "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=800"),
            BannerItem("Lokal Cirebon Pride", "Koleksi Hoodie Batik Megamendung", "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800"),
            BannerItem("Gratis Ongkir Kesambi", "Promo Khusus Wilayah Kota Cirebon", "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800")
        )
        binding.vpBanners.adapter = BannerAdapter(banners)
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            val intent = Intent(requireContext(), ProductListActivity::class.java).apply {
                putExtra("CATEGORY_ID", category.id)
                putExtra("CATEGORY_NAME", category.name)
            }
            startActivity(intent)
        }
        binding.rvHomeCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeCategories.adapter = categoryAdapter

        flashSaleAdapter = ProductAdapter { product ->
            openDetail(product.id)
        }
        binding.rvFlashSale.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFlashSale.adapter = flashSaleAdapter

        productAdapter = ProductAdapter { product ->
            openDetail(product.id)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = productAdapter
    }

    private fun openDetail(productId: String) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra("PRODUCT_ID", productId)
        }
        startActivity(intent)
    }

    private fun setupListeners() {
        binding.btnCartBadge.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(com.example.shopiku.R.id.nav_cart)
        }

        binding.tvSeeAllCategories.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(com.example.shopiku.R.id.nav_category)
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                val intent = Intent(requireContext(), ProductListActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", query)
                }
                startActivity(intent)
                true
            } else false
        }

        binding.btnRetry.setOnClickListener {
            loadHomeData()
        }
    }

    private fun loadHomeData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE

        lifecycleScope.launch {
            val catRes = productRepository.getCategories()
            if (catRes is Resource.Success) {
                categoryAdapter.submitList(catRes.data)
            }

            val flashRes = productRepository.getFlashSaleProducts()
            if (flashRes is Resource.Success) {
                flashSaleAdapter.submitList(flashRes.data)
            }

            val prodRes = productRepository.getProducts()
            binding.progressBar.visibility = View.GONE

            if (prodRes is Resource.Success) {
                productAdapter.submitList(prodRes.data)
            } else if (prodRes is Resource.Error) {
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = prodRes.message
            }
        }
    }

    private fun updateCartBadge() {
        val cartCount = sessionManager.getCartItems().sumOf { it.quantity }
        if (cartCount > 0) {
            binding.tvCartCount.visibility = View.VISIBLE
            binding.tvCartCount.text = cartCount.toString()
        } else {
            binding.tvCartCount.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}