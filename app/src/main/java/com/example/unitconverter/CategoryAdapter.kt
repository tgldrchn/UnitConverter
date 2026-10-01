package com.example.unitconverter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.unitconverter.logic.Category

@DrawableRes
fun Category.iconRes(): Int = when (this) {
    Category.LENGTH -> R.drawable.ic_length
    Category.AREA -> R.drawable.ic_area
    Category.VOLUME -> R.drawable.ic_volume
    Category.MASS -> R.drawable.ic_mass
    Category.TIME -> R.drawable.ic_time
    Category.SPEED -> R.drawable.ic_speed
    Category.TEMPERATURE -> R.drawable.ic_temperature
    Category.DENSITY -> R.drawable.ic_density
    Category.ENERGY -> R.drawable.ic_energy
    Category.ANGLE -> R.drawable.ic_angle
    Category.WEIGHT -> R.drawable.ic_weight
    Category.FUEL -> R.drawable.ic_fuel
}

class CategoryAdapter(
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var items: List<Category> = emptyList()

    fun submit(newItems: List<Category>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.categoryIcon)
        private val name: TextView = view.findViewById(R.id.categoryName)

        fun bind(category: Category) {
            icon.setImageResource(category.iconRes())
            name.text = category.title
            itemView.setOnClickListener { onClick(category) }
        }
    }
}
