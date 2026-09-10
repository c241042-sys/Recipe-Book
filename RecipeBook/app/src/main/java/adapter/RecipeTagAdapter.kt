package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeTagAdapter(
    private var tagList: List<Tag>,
    private val onTagClick: (Tag) -> Unit
) : RecyclerView.Adapter<RecipeTagAdapter.TagViewHolder>() {

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
                    R.layout.item_tag,
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

        holder.itemView.setOnClickListener {

            onTagClick(tag)
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    fun updateList(
        newList: List<Tag>
    ) {

        tagList =
            newList

        notifyDataSetChanged()
    }
}