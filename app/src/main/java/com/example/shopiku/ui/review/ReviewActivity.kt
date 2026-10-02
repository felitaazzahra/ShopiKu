package com.example.shopiku.ui.review

import android.os.Bundle
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiku.data.model.Review
import com.example.shopiku.data.repository.ProductRepository
import com.example.shopiku.databinding.ActivityReviewBinding
import com.example.shopiku.util.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private val productRepository = ProductRepository()
    private lateinit var reviewAdapter: ReviewAdapter

    private var productId: String = "p1"
    private var reviewList = mutableListOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("PRODUCT_ID") ?: "p1"

        setupUI()
        loadReviews()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        reviewAdapter = ReviewAdapter()
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = reviewAdapter

        binding.btnWriteReview.setOnClickListener {
            showWriteReviewDialog()
        }
    }

    private fun loadReviews() {
        lifecycleScope.launch {
            val res = productRepository.getReviewsForProduct(productId)
            if (res is Resource.Success) {
                reviewList = res.data.toMutableList()
                reviewAdapter.submitList(reviewList)
            }
        }
    }

    private fun showWriteReviewDialog() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val ratingBar = RatingBar(this).apply {
            numStars = 5
            rating = 5.0f
            stepSize = 1.0f
        }
        val etComment = EditText(this).apply {
            hint = "Tulis pengalaman belanja Anda di ShopiKu..."
        }

        container.addView(ratingBar)
        container.addView(etComment)

        MaterialAlertDialogBuilder(this)
            .setTitle("Tulis Ulasan Produk")
            .setView(container)
            .setPositiveButton("Kirim") { dialog, _ ->
                val commentText = etComment.text.toString().trim()
                if (commentText.isNotBlank()) {
                    val newReview = Review(
                        id = "rev_" + System.currentTimeMillis(),
                        productId = productId,
                        userName = "Pembeli ShopiKu",
                        rating = ratingBar.rating,
                        comment = commentText,
                        variantInfo = "Varian: Hitam / 42",
                        createdAt = "Baru saja"
                    )
                    reviewList.add(0, newReview)
                    reviewAdapter.submitList(reviewList.toList())
                    Toast.makeText(this, "Terima kasih atas ulasan Anda!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}