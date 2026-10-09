package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
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

        val tagsScroll: HorizontalScrollView =
            itemView.findViewById(R.id.recipeTagsScroll)

        val tagsContainer: LinearLayout =
            itemView.findViewById(R.id.recipeTagsContainer)

        val favoriteMark: TextView =
            itemView.findViewById(R.id.favoriteMark)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)

        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {
        val recipe = recipeList[position]

        // レシピ画像
        if (!recipe.imageUri.isNullOrEmpty()) {
            holder.image.setImageURI(Uri.parse(recipe.imageUri))
        } else {
            holder.image.setImageResource(
                R.drawable.recipe_placeholder
            )
        }

        // レシピ名・説明・調理時間
        holder.name.text = recipe.name
        holder.description.text = recipe.description
        holder.time.text = "◷ ${recipe.cookTime}分"

        // タグを丸いラベルとして表示
        holder.tagsContainer.removeAllViews()

        if (recipe.tags.isEmpty()) {
            holder.tagsScroll.visibility = View.GONE
        } else {
            holder.tagsScroll.visibility = View.VISIBLE

            val density =
                holder.itemView.resources.displayMetrics.density

            recipe.tags.forEach { tag ->
                val chip = TextView(holder.itemView.context).apply {
                    text = tag
                    textSize = 10f
                    setTextColor(
                        android.graphics.Color.parseColor("#98651F")
                    )
                    setPadding(
                        (8 * density).toInt(),
                        (4 * density).toInt(),
                        (8 * density).toInt(),
                        (4 * density).toInt()
                    )
                    setBackgroundResource(
                        R.drawable.tag_chip_background
                    )
                    maxLines = 1
                }

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = (5 * density).toInt()
                }

                holder.tagsContainer.addView(chip, params)
            }
        }

        // お気に入りマーク
        holder.favoriteMark.visibility =
            if (recipe.favorite) View.VISIBLE else View.GONE

        // カードを押すとレシピ詳細へ移動
        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun updateList(newList: List<Recipe>) {
        recipeList = newList
        notifyDataSetChanged()
    }
}