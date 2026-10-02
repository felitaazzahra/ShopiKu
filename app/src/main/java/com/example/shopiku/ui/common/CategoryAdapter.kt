package com.example.shopiku.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopiku.data.model.Category
import com.example.shopiku.databinding.ItemCategoryCardBinding

class CategoryAdapter(
    private var categories: List<Category> = emptyList(),
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    fun submitList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }

    class CategoryViewHolder(val binding: ItemCategoryCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryTitle.text = category.name

        if (category.iconUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(category.iconUrl)
                .centerCrop()
                .into(holder.binding.ivCategoryIcon)
        }

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}