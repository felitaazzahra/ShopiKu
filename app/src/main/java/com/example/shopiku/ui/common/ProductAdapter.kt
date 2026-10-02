package com.example.shopiku.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopiku.data.model.Product
import com.example.shopiku.databinding.ItemProductCardBinding
import com.example.shopiku.util.CurrencyUtils

class ProductAdapter(
    private var products: List<Product> = emptyList(),
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun submitList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    class ProductViewHolder(val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.tvCategoryName.text = product.categoryName
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductPrice.text = CurrencyUtils.formatRupiah(product.price)
        holder.binding.tvRating.text = product.rating.toString()
        holder.binding.tvSoldCount.text = "• ${product.soldCount} Terjual"

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .centerCrop()
            .into(holder.binding.ivProductImage)

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int = products.size
}