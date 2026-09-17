package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeDetailTagAdapter(
    private var tagList: List<RecipeTag>,
    private val onLongClick: (RecipeTag) -> Unit
) : RecyclerView.Adapter<RecipeDetailTagAdapter.TagViewHolder>() {

    class TagViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val name: TextView =
            itemView.findViewById(
                R.id.tagName
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_recipe_tag,
                    parent,
                    false
                )

        return TagViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TagViewHolder,
        position: Int
    ) {

        val tag =
            tagList[position]

        holder.name.text =
            "#${tag.name}"

        holder.itemView.setOnLongClickListener {

            onLongClick(tag)

            true
        }
    }

    override fun getItemCount(): Int {

        return tagList.size
    }

    fun updateList(
        newList: List<RecipeTag>
    ) {

        tagList =
            newList

        notifyDataSetChanged()
    }
}