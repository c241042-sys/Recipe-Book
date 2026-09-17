package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.RecipeIngredient

class RecipeDetailIngredientAdapter(
    private var ingredientList: List<RecipeIngredient>,
    private val onClick: (RecipeIngredient) -> Unit,
    private val onLongClick: (RecipeIngredient) -> Unit
) : RecyclerView.Adapter<RecipeDetailIngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val name: TextView =
            itemView.findViewById(
                R.id.ingredientName
            )

        val amount: TextView =
            itemView.findViewById(
                R.id.ingredientAmount
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_recipe_detail_ingredient,
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
            ingredient.ingredientName

        holder.amount.text =
            ingredient.amount

        holder.itemView.setOnClickListener {

            onClick(ingredient)
        }

        holder.itemView.setOnLongClickListener {

            onLongClick(ingredient)

            true
        }
    }

    override fun getItemCount(): Int {

        return ingredientList.size
    }

    fun updateList(
        newList: List<RecipeIngredient>
    ) {

        ingredientList =
            newList

        notifyDataSetChanged()
    }
}