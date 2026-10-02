package com.example.shopiku.ui.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiku.data.model.Review
import com.example.shopiku.databinding.ItemReviewBinding

class ReviewAdapter(
    private var reviews: List<Review> = emptyList()
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    fun submitList(newList: List<Review>) {
        reviews = newList
        notifyDataSetChanged()
    }

    class ReviewViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.binding.tvUserName.text = review.userName
        holder.binding.tvDate.text = review.createdAt
        holder.binding.rbUserRating.rating = review.rating
        holder.binding.tvVariantInfo.text = review.variantInfo
        holder.binding.tvComment.text = review.comment
    }

    override fun getItemCount(): Int = reviews.size
}