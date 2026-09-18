package com.example.recipebook

import adapter.RecipeDetailIngredientAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private var recipeId: Int = -1

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var recipeTime: TextView
    private lateinit var recipeDifficulty: TextView
    private lateinit var recipeServings: TextView
    private lateinit var favoriteButton: TextView
    private lateinit var stepAdapter: StepAdapter

    // =========================
    // 削除
    // =========================
    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
            .setTitle("レシピを削除しますか？")
            .setMessage(
                "このレシピ、材料との関連付け、タグとの関連付け、手順、調理履歴も削除されます。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "削除"
            ) { _, _ ->

                deleteRecipe()
            }
            .show()
    }

    private fun deleteRecipe() {

        val result =
            dbHelper.deleteRecipe(
                recipeId
            )

        if (result) {

            Toast.makeText(
                this,
                "レシピを削除しました",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "削除に失敗しました",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // お気に入り
    // =========================
    private fun updateFavoriteButton(
        favorite: Boolean
    ) {

        favoriteButton.text =
            if (favorite) {
                "♥"
            } else {
                "♡"
            }
    }

    private fun toggleFavorite() {

        val currentFavorite =
            dbHelper.isFavorite(recipeId)

        val newFavorite =
            !currentFavorite

        val result =
            dbHelper.updateFavorite(
                recipeId = recipeId,
                favorite = newFavorite
            )

        if (result > 0) {

            updateFavoriteButton(
                newFavorite
            )

            if (newFavorite) {

                Toast.makeText(
                    this,
                    "お気に入りに追加しました",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "お気に入りから削除しました",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                "更新に失敗しました",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private lateinit var tagAdapter:
            RecipeDetailTagAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recipe_detail)

        // recipeIdを受け取る
        recipeId = intent.getIntExtra(
            "recipe_id",
            -1
        )

        // IDが取得できなかった場合
        if (recipeId == -1) {

            Toast.makeText(
                this,
                "レシピを取得できませんでした",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // DB
        dbHelper = RecipeDBHelper(this)

        // =========================
        // View取得
        // =========================

        recipeImage =
            findViewById(R.id.recipeImage)

        recipeName =
            findViewById(R.id.recipeName)

        recipeDescription =
            findViewById(R.id.recipeDescription)

        recipeTime =
            findViewById(R.id.recipeTime)

        recipeDifficulty =
            findViewById(R.id.recipeDifficulty)

        recipeServings =
            findViewById(R.id.recipeServings)

        favoriteButton =
            findViewById(R.id.favoriteButton)

        // =========================
        // 作り方RecyclerView
        // =========================

        val stepRecyclerView =
            findViewById<RecyclerView>(
                R.id.stepRecyclerView
            )

        stepAdapter =
            StepAdapter(
                emptyList(),

                // 編集
                { step ->

                    val intent =
                        Intent(
                            this,
                            StepEditActivity::class.java
                        )

                    intent.putExtra(
                        "recipe_id",
                        recipeId
                    )

                    intent.putExtra(
                        "step_id",
                        step.id
                    )

                    startActivity(intent)
                },

                // 上へ
                { step ->

                    if (
                        dbHelper.moveStepUp(
                            recipeId,
                            step.id
                        )
                    ) {

                        loadRecipe()
                    }
                },

                // 下へ
                { step ->

                    if (
                        dbHelper.moveStepDown(
                            recipeId,
                            step.id
                        )
                    ) {

                        loadRecipe()
                    }
                }
            )

        stepRecyclerView.adapter =
            stepAdapter

        stepRecyclerView.layoutManager =
            LinearLayoutManager(this)

        // =========================
        // 戻る
        // =========================

        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }

        // =========================
        // お気に入り
        // =========================

        favoriteButton.setOnClickListener {

            toggleFavorite()
        }

        // =========================
        // 編集
        // =========================

        findViewById<Button>(
            R.id.editButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    RecipeEditActivity::class.java
                )

            intent.putExtra(
                "recipe_id",
                recipeId
            )

            startActivity(intent)
        }

        // =========================
        // 削除
        // =========================

        findViewById<Button>(
            R.id.deleteButton
        ).setOnClickListener {

            showDeleteDialog()
        }

        // =========================
        // 作る
        // =========================

        findViewById<Button>(
            R.id.cookButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    CookingActivity::class.java
                )

            intent.putExtra(
                "recipe_id",
                recipeId
            )

            startActivity(intent)
        }

        // =========================
        // 材料
        // =========================

        findViewById<TextView>(
            R.id.addIngredientButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    AddRecipeIngredientActivity::class.java
                )

            intent.putExtra(
                "recipe_id",
                recipeId
            )

            startActivity(intent)
        }

        // =========================
        // タグ
        // =========================

        findViewById<TextView>(
            R.id.addTagButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    AddRecipeTagActivity::class.java
                )

            intent.putExtra(
                "recipe_id",
                recipeId
            )

            startActivity(intent)
        }

        val tagRecyclerView =
            findViewById<RecyclerView>(
                R.id.tagRecyclerView
            )

        tagAdapter =
            RecipeDetailTagAdapter(
                emptyList()
            ) { tag ->

                showDeleteTagDialog(tag)
            }

        tagRecyclerView.adapter =
            tagAdapter

        tagRecyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        // =========================
        // 手順追加
        // =========================

        findViewById<TextView>(
            R.id.addStepButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    StepEditActivity::class.java
                )

            intent.putExtra(
                "recipe_id",
                recipeId
            )

            startActivity(intent)
        }

        // =========================
        // レシピ表示
        // =========================

        loadRecipe()
    }

    private fun loadRecipe() {

        val recipe =
            dbHelper.getRecipeById(recipeId)

        // レシピが見つからない
        if (recipe == null) {

            Toast.makeText(
                this,
                "レシピが見つかりません",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        // =========================
        // 名前
        // =========================

        recipeName.text =
            recipe.name

        // ========================
        // 説明
        // =========================

        recipeDescription.text =
            recipe.description

        // =========================
        // 調理時間
        // =========================

        recipeTime.text =
            "${recipe.cookTime}分"

        // =========================
        // 難易度
        // =========================

        recipeDifficulty.text =
            recipe.difficulty

        // =========================
        // 人数
        // =========================

        recipeServings.text =
            "${recipe.servings}人分"

        // =========================
        // 画像
        // =========================

        if (!recipe.imageUri.isNullOrEmpty()) {

            recipeImage.setImageURI(
                Uri.parse(recipe.imageUri)
            )

        } else {

            recipeImage.setImageResource(
                R.drawable.recipe_placeholder
            )
        }

        // =========================
        // 材料
        // =========================

        val ingredients =
            dbHelper.getIngredientsByRecipeId(
                recipeId
            )

        val ingredientContainer =
            findViewById<LinearLayout>(
                R.id.ingredientContainer
            )

        val ingredientEmptyText =
            findViewById<TextView>(
                R.id.ingredientEmptyText
            )

        ingredientContainer.removeAllViews()

        if (ingredients.isEmpty()) {

            ingredientEmptyText.visibility =
                View.VISIBLE

        } else {

            ingredientEmptyText.visibility =
                View.GONE

            for (ingredient in ingredients) {

                val itemView =
                    layoutInflater.inflate(
                        R.layout.item_recipe_detail_ingredient,
                        ingredientContainer,
                        false
                    )

                val nameText =
                    itemView.findViewById<TextView>(
                        R.id.ingredientName
                    )

                val amountText =
                    itemView.findViewById<TextView>(
                        R.id.ingredientAmount
                    )

                nameText.text =
                    ingredient.ingredientName

                amountText.text =
                    ingredient.amount


                // タップ → 分量編集
                itemView.setOnClickListener {

                    showEditIngredientAmountDialog(
                        ingredient
                    )
                }


                // 長押し → 削除
                itemView.setOnLongClickListener {

                    showDeleteIngredientDialog(
                        ingredient
                    )

                    true
                }


                ingredientContainer.addView(
                    itemView
                )
            }
        }

        // =========================
        // タグ
        // =========================

        val tags =
            dbHelper.getTagsByRecipeId(
                recipeId
            )

        tagAdapter.updateList(
            tags
        )

        val tagEmptyText =
            findViewById<TextView>(
                R.id.tagEmptyText
            )

        if (tags.isEmpty()) {

            tagEmptyText.visibility =
                View.VISIBLE

        } else {

            tagEmptyText.visibility =
                View.GONE
        }

        // =========================
        // 作り方
        // =========================

        val steps =
            dbHelper.getStepsByRecipeId(
                recipeId
            )

        stepAdapter.updateList(
            steps
        )

        val stepText =
            findViewById<TextView>(
                R.id.stepText
            )

        if (steps.isEmpty()) {

            stepText.visibility =
                android.view.View.VISIBLE

            stepText.text =
                "作り方はまだ登録されていません"

        } else {

            stepText.visibility =
                android.view.View.GONE
        }

        // =========================
        // お気に入り状態
        // =========================

        val favorite =
            dbHelper.isFavorite(recipeId)

        updateFavoriteButton(favorite)
    }

    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized) {
            loadRecipe()
        }
    }

    // =========================
    //　分量編集
    // =========================
    private fun showEditIngredientAmountDialog(
        ingredient: RecipeIngredient
    ) {

        val input =
            EditText(this)

        input.setText(
            ingredient.amount
        )

        input.setSingleLine(true)

        input.hint =
            "例：200g、2個、大さじ1"


        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "${ingredient.ingredientName}の分量"
                )
                .setView(input)
                .setNegativeButton(
                    "キャンセル",
                    null
                )
                .setPositiveButton(
                    "保存",
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
                    dbHelper.updateRecipeIngredientAmount(
                        recipeId = recipeId,
                        ingredientId =
                            ingredient.ingredientId,
                        amount = amount
                    )


                if (result > 0) {

                    dialog.dismiss()

                    loadRecipe()

                    Toast.makeText(
                        this,
                        "分量を更新しました",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "更新に失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

    // =========================
    //　材料削除
    // =========================
    private fun showDeleteIngredientDialog(
        ingredient: RecipeIngredient
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                "材料を削除しますか？"
            )
            .setMessage(
                "${ingredient.ingredientName}をこのレシピから削除します。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "削除"
            ) { _, _ ->

                val result =
                    dbHelper.deleteIngredientFromRecipe(
                        recipeId = recipeId,
                        ingredientId =
                            ingredient.ingredientId
                    )

                if (result > 0) {

                    loadRecipe()

                    Toast.makeText(
                        this,
                        "材料を削除しました",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "削除に失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // =========================
    //　タグ削除
    // =========================
    private fun showDeleteTagDialog(
        tag: RecipeTag
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                "タグを削除しますか？"
            )
            .setMessage(
                "#${tag.name}をこのレシピから削除します。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "削除"
            ) { _, _ ->

                val result =
                    dbHelper.deleteTagFromRecipe(
                        recipeId = recipeId,
                        tagId = tag.id
                    )

                if (result > 0) {

                    loadRecipe()

                    Toast.makeText(
                        this,
                        "タグを削除しました",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "削除に失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}

