package com.example.recipebook

import adapter.RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: RecipeAdapter
    private lateinit var dbHelper: RecipeDBHelper

    private var recipeList = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BottomNavigation.setup(this, BottomNavigation.Tab.HOME)

        recyclerView = findViewById(R.id.recipeRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        dbHelper = RecipeDBHelper(this)

        // レシピ一覧
        adapter = RecipeAdapter(recipeList) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("recipe_id", recipe.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                    searchRecipe(s.toString())
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )

        // 右上の追加ボタン
        findViewById<TextView>(R.id.addButton).setOnClickListener {
            startActivity(
                Intent(this, RecipeEditActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecipes()
    }

    private fun loadRecipes() {
        recipeList = dbHelper.getAllRecipes()
        searchRecipe(searchEditText.text.toString())
    }

    private fun searchRecipe(keyword: String) {
        val searchText = keyword.trim()

        if (searchText.isEmpty()) {
            adapter.updateList(recipeList)
            return
        }

        val result = recipeList.filter { recipe ->
            recipe.name.contains(searchText, ignoreCase = true) ||
                    recipe.description.contains(searchText, ignoreCase = true) ||
                    dbHelper.getTagsByRecipeId(recipe.id).any { tag ->
                        tag.name.contains(searchText, ignoreCase = true)
                    }
        }

        adapter.updateList(result)
    }
}