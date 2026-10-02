package com.example.shopiku.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopiku.data.model.CartItem
import com.example.shopiku.databinding.ItemCartBinding
import com.example.shopiku.util.CurrencyUtils

class CartAdapter(
    private var items: List<CartItem> = emptyList(),
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit,
    private val onSelectToggle: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun submitList(newList: List<CartItem>) {
        items = newList
        notifyDataSetChanged()
    }

    class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.binding.cbSelect.isChecked = item.isSelected
        holder.binding.tvProductName.text = item.product.name
        holder.binding.tvVariant.text = "Varian: ${item.variant}"
        holder.binding.tvPrice.text = CurrencyUtils.formatRupiah(item.totalPrice)
        holder.binding.tvQuantity.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.product.imageUrl)
            .centerCrop()
            .into(holder.binding.ivProductImage)

        holder.binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onSelectToggle()
        }

        holder.binding.btnMinus.setOnClickListener {
            onQuantityChange(item, item.quantity - 1)
        }

        holder.binding.btnPlus.setOnClickListener {
            onQuantityChange(item, item.quantity + 1)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}