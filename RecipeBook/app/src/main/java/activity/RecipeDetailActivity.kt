package com.example.recipebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.database.RecipeDBHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private var recipeId: Int = -1

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var recipeTime: TextView
    private lateinit var favoriteButton: TextView
    private lateinit var ingredientText: TextView
    private lateinit var stepAdapter: StepAdapter

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

        favoriteButton =
            findViewById(R.id.favoriteButton)

        ingredientText =
            findViewById(R.id.ingredientText)


        // =========================
        // 作り方RecyclerView
        // =========================

        val stepRecyclerView =
            findViewById<RecyclerView>(
                R.id.stepRecyclerView
            )

        stepAdapter =
            StepAdapter(emptyList())

        stepRecyclerView.adapter =
            stepAdapter

        stepRecyclerView.layoutManager =
            LinearLayoutManager(this)


        // =========================
        // レシピ表示
        // =========================

        loadRecipe()


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

            Toast.makeText(
                this,
                "お気に入り機能は次に実装します",
                Toast.LENGTH_SHORT
            ).show()
        }


        // =========================
        // 編集
        // =========================

        findViewById<Button>(
            R.id.editButton
        ).setOnClickListener {

            Toast.makeText(
                this,
                "編集機能は次に実装します",
                Toast.LENGTH_SHORT
            ).show()
        }


        // =========================
        // 削除
        // =========================

        findViewById<Button>(
            R.id.deleteButton
        ).setOnClickListener {

            Toast.makeText(
                this,
                "削除機能は次に実装します",
                Toast.LENGTH_SHORT
            ).show()
        }


        // =========================
        // 作る
        // =========================

        findViewById<Button>(
            R.id.cookButton
        ).setOnClickListener {

            Toast.makeText(
                this,
                "調理画面は次に実装します",
                Toast.LENGTH_SHORT
            ).show()
        }


        // =========================
        // 材料追加
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
        // タグ追加
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


        // =========================
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
            dbHelper.getIngredientsByRecipeId(               recipeId
            )

        if (ingredients.isEmpty()) {

            ingredientText.text =
                "材料はまだ登録されていません"

        } else {

            ingredientText.text =
                ingredients.joinToString("\n") {

                    "${it.ingredientName}    ${it.amount}"
                }
        }

        // =========================
        // タグ
        // =========================

        val tagText =
            findViewById<TextView>(
                R.id.tagText
            )

        val tags =
            dbHelper.getTagsByRecipeId(
                recipeId
            )

        if (tags.isEmpty()) {

            tagText.text =
                "タグはまだ登録されていません"

        } else {

            tagText.text =
                tags.joinToString("   ") {
                    "#${it.name}"
                }
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
    }

    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized) {
            loadRecipe()
        }
    }
}