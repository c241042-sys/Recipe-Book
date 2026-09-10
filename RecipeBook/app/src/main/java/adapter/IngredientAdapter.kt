package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientAdapter(
    private var ingredientList: List<Ingredient>
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val name: TextView =
            itemView.findViewById(
                R.id.ingredientName
            )

        val category: TextView =
            itemView.findViewById(
                R.id.ingredientCategory
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_ingredient,
                    parent,
                    false
                )

        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: IngredientViewHolder,
        position: Int
    ) {

        val ingredient =
            ingredientList[position]

        holder.name.text =
            ingredient.name

        holder.category.text =
            ingredient.category
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    fun updateList(
        newList: List<Ingredient>
    ) {

        ingredientList = newList

        notifyDataSetChanged()
    }
}