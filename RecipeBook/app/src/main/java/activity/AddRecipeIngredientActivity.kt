package com.example.recipebook

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class AddRecipeIngredientActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var adapter: RecipeIngredientAdapter
    private lateinit var searchEditText: EditText

    private var recipeId: Int = -1

    private var ingredientList =
        mutableListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_add_recipe_ingredient
        )

        recipeId =
            intent.getIntExtra(
                "recipe_id",
                -1
            )

        if (recipeId == -1) {

            Toast.makeText(
                this,
                "レシピIDを取得できませんでした",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

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


        // Adapter
        adapter =
            RecipeIngredientAdapter(
                ingredientList
            ) { ingredient ->

                showAmountDialog(
                    ingredient
                )
            }

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


        // 検索
        searchEditText.addTextChangedListener(
            object : TextWatcher {

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
                    s: Editable?
                ) {
                }
            }
        )


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

        val text =
            keyword.trim()

        if (text.isEmpty()) {

            adapter.updateList(
                ingredientList
            )

            return
        }

        val result =
            ingredientList.filter { ingredient ->

                ingredient.name.contains(
                    text,
                    ignoreCase = true
                ) ||

                        ingredient.category.contains(
                            text,
                            ignoreCase = true
                        )
            }

        adapter.updateList(result)
    }


    private fun showAmountDialog(
        ingredient: Ingredient
    ) {

        // 二重登録チェック
        if (
            dbHelper.isIngredientAddedToRecipe(
                recipeId,
                ingredient.id
            )
        ) {

            Toast.makeText(
                this,
                "この材料はすでに登録されています",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val input =
            EditText(this)

        input.hint =
            "例：200g、2個、大さじ1"

        input.setSingleLine(true)


        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "${ingredient.name}の分量"
                )
                .setView(input)
                .setNegativeButton(
                    "キャンセル",
                    null
                )
                .setPositiveButton(
                    "追加",
                    null
                )
                .create()


        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val amount =
                    input.text
                        .toString()
                        .trim()

                if (amount.isEmpty()) {

                    input.error =
                        "分量を入力してください"

                    return@setOnClickListener
                }


                val result =
                    dbHelper.addIngredientToRecipe(
                        recipeId = recipeId,
                        ingredientId = ingredient.id,
                        amount = amount,
                        displayOrder = getNextDisplayOrder()
                    )


                if (result != -1L) {

                    Toast.makeText(
                        this,
                        "${ingredient.name}を追加しました",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()

                } else {

                    Toast.makeText(
                        this,
                        "追加に失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }


    private fun getNextDisplayOrder(): Int {

        val ingredients =
            dbHelper.getIngredientsByRecipeId(
                recipeId
            )

        return ingredients.size
    }
}