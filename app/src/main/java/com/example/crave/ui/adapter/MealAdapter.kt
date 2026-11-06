package com.example.crave.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.crave.data.model.MealModel
import com.example.crave.databinding.ItemMealBinding
import com.squareup.picasso.Picasso

/**
 * RecyclerView Adapter. It's reused in BOTH screens (API list and cookbook).
 *
 * It only "paints" data: thumbnail (Picasso) and name. It decides nothing: when a card
 * is tapped it notifies outward through [onMealClick] and the Fragment decides what to do (open the detail).
 *
 * Uses ListAdapter + DiffUtil: when a new list arrives, only the rows that changed are redrawn.
 */
class MealAdapter(
    private val onMealClick: (MealModel) -> Unit
) : ListAdapter<MealModel, MealAdapter.MealViewHolder>(DiffCallback) {

    inner class MealViewHolder(
        private val binding: ItemMealBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // The listener is set only once per ViewHolder (not on every bind).
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMealClick(getItem(position))
                }
            }
        }

        fun bind(meal: MealModel) {
            binding.tvMealName.text = meal.name

            Picasso.get()
                .load(meal.thumbnail)
                .placeholder(android.R.drawable.ic_menu_gallery)  // while downloading
                .error(android.R.drawable.ic_menu_report_image)   // if it fails (e.g. no internet)
                .into(binding.ivMeal)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<MealModel>() {
        override fun areItemsTheSame(oldItem: MealModel, newItem: MealModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MealModel, newItem: MealModel): Boolean =
            oldItem == newItem
    }
}