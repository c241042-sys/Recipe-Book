package com.example.recipebook

import adapter.RecipeAdapter
import activity.RecipeEditActivity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: RecipeAdapter

    private lateinit var dbHelper: RecipeDBHelper

    private var recipeList =
        mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView =
            findViewById(R.id.recipeRecyclerView)

        searchEditText =
            findViewById(R.id.searchEditText)

        dbHelper =
            RecipeDBHelper(this)


        // Adapter
        adapter =
            RecipeAdapter(recipeList) { recipe ->

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


        // 検索
        searchEditText.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    searchRecipe(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )


        // 追加ボタン
        findViewById<
                com.google.android.material.floatingactionbutton.FloatingActionButton
                >(R.id.addButton)
            .setOnClickListener {

                val intent =
                    Intent(
                        this,
                        RecipeEditActivity::class.java
                    )

                startActivity(intent)
            }

        findViewById<TextView>(
            R.id.ingredientManageButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    IngredientActivity::class.java
                )
            )
        }

        // タグ
        findViewById<TextView>(
            R.id.tagManageButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TagActivity::class.java
                )
            )
        }
    }


    override fun onResume() {
        super.onResume()

        // ホーム画面に戻ってきたらDBを再読み込み
        loadRecipes()
    }


    private fun loadRecipes() {

        recipeList =
            dbHelper.getAllRecipes()

        adapter.updateList(
            recipeList
        )
    }


    private fun searchRecipe(
        keyword: String
    ) {

        val searchText =
            keyword.trim()

        if (searchText.isEmpty()) {

            adapter.updateList(
                recipeList
            )

            return
        }

        val result =
            recipeList.filter { recipe ->

                // レシピ名
                if (
                    recipe.name.contains(
                        searchText,
                        ignoreCase = true
                    )
                ) {
                    true
                }

                // 説明
                else if (
                    recipe.description.contains(
                        searchText,
                        ignoreCase = true
                    )
                ) {
                    true
                }

                // タグ
                else {

                    val tags =
                        dbHelper.getTagsByRecipeId(
                            recipe.id
                        )

                    tags.any {

                        it.name.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }
                }
            }

        adapter.updateList(result)
    }
}