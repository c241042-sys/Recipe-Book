package com.example.recipebook

import adapter.RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class FavoriteActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var adapter: RecipeAdapter

    private lateinit var emptyText: TextView

    private var recipeList =
        mutableListOf<Recipe>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_favorite
        )

        BottomNavigation.setup(
            this, BottomNavigation.Tab.FAVORITES
        )

        dbHelper =
            RecipeDBHelper(this)


        val recyclerView =
            findViewById<RecyclerView>(
                R.id.favoriteRecyclerView
            )

        emptyText =
            findViewById(
                R.id.emptyText
            )


        adapter =
            RecipeAdapter(
                recipeList
            ) { recipe ->

                val intent =
                    Intent(
                        this,
                        RecipeDetailActivity::class.java
                    )

                intent.putExtra(
                    "recipe_id",
                    recipe.id
                )

                startActivity(intent)
            }


        recyclerView.adapter =
            adapter

        recyclerView.layoutManager =
            LinearLayoutManager(this)
    }


    override fun onResume() {
        super.onResume()

        loadFavoriteRecipes()
    }


    private fun loadFavoriteRecipes() {

        recipeList =
            dbHelper.getFavoriteRecipes()

        adapter.updateList(
            recipeList
        )


        // 0件ならメッセージ
        if (recipeList.isEmpty()) {

            emptyText.visibility =
                TextView.VISIBLE

        } else {

            emptyText.visibility =
                TextView.GONE
        }
    }
}