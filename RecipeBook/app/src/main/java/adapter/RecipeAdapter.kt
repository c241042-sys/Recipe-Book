package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.Recipe

class RecipeAdapter(
    private var recipeList: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.recipeImage)

        val name: TextView =
            itemView.findViewById(R.id.recipeName)

        val description: TextView =
            itemView.findViewById(R.id.recipeDescription)

        val time: TextView =
            itemView.findViewById(R.id.recipeTime)

        val tags: TextView =
            itemView.findViewById(R.id.recipeTags)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_recipe,
                    parent,
                    false
                )

        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {

        val recipe = recipeList[position]

        // 画像
        if (!recipe.imageUri.isNullOrEmpty()) {

            holder.image.setImageURI(
                Uri.parse(recipe.imageUri)
            )

        } else {

            holder.image.setImageResource(
                R.drawable.recipe_placeholder
            )
        }

        // 名前
        holder.name.text =
            recipe.name

        // 説明
        holder.description.text =
            recipe.description

        // 調理時間
        holder.time.text =
            "◷ ${recipe.cookTime}分"

        // タグ
        if (recipe.tags.isEmpty()) {

            holder.tags.text = ""

        } else {

            holder.tags.text =
                recipe.tags.joinToString("   ")
        }

        // タップ
        holder.itemView.setOnClickListener {

            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun updateList(
        newList: List<Recipe>
    ) {

        recipeList = newList

        notifyDataSetChanged()
    }
}