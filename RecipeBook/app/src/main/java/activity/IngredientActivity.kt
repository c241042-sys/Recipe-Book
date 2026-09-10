package com.example.recipebook

import activity.IngredientEditActivity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class IngredientActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var adapter: IngredientAdapter
    private lateinit var searchEditText: EditText

    private var ingredientList =
        mutableListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_ingredient
        )

        dbHelper =
            RecipeDBHelper(this)

        searchEditText =
            findViewById(
                R.id.searchIngredientEditText
            )

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.ingredientRecyclerView
            )

        adapter =
            IngredientAdapter(
                ingredientList
            )

        recyclerView.adapter =
            adapter

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        // 戻る
        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }

        // 材料追加
        findViewById<
                com.google.android.material.floatingactionbutton.FloatingActionButton
                >(R.id.addIngredientButton)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        IngredientEditActivity::class.java
                    )
                )
            }

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

                    searchIngredient(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()

        loadIngredients()
    }

    private fun loadIngredients() {

        ingredientList =
            dbHelper.getAllIngredients()

        adapter.updateList(
            ingredientList
        )
    }

    private fun searchIngredient(
        keyword: String
    ) {

        val searchText =
            keyword.trim()

        if (searchText.isEmpty()) {

            adapter.updateList(
                ingredientList
            )

            return
        }

        val result =
            ingredientList.filter { ingredient ->

                ingredient.name.contains(
                    searchText,
                    ignoreCase = true
                ) ||

                        ingredient.category.contains(
                            searchText,
                            ignoreCase = true
                        )
            }

        adapter.updateList(result)
    }
}