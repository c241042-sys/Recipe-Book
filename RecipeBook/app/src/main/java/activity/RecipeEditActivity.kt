package com.example.recipebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.database.RecipeDBHelper

class RecipeEditActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText

    private lateinit var cookTimeSpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var servingsSpinner: Spinner

    private lateinit var recipeImage: ImageView

    private lateinit var difficultyAdapter: ArrayAdapter<String>

    private lateinit var dbHelper: RecipeDBHelper

    private var recipeId: Int = -1

    private var selectedImageUri: Uri? = null

    // 編集モードかどうか
    private var isEditMode = false

    // 画像選択
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                try {

                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                } catch (e: SecurityException) {

                    e.printStackTrace()
                }

                recipeImage.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_recipe_edit
        )

        // =========================
        // View取得
        // =========================

        nameEditText =
            findViewById(R.id.nameEditText)

        descriptionEditText =
            findViewById(R.id.descriptionEditText)

        cookTimeSpinner =
            findViewById(R.id.cookTimeSpinner)

        difficultySpinner =
            findViewById(R.id.difficultySpinner)

        servingsSpinner =
            findViewById(R.id.servingsSpinner)

        recipeImage =
            findViewById(R.id.recipeImage)

        dbHelper =
            RecipeDBHelper(this)

        // =========================
        // 調理時間
        // =========================

        val cookTimes = arrayOf(
            "5分",
            "10分",
            "15分",
            "20分",
            "25分",
            "30分",
            "40分",
            "50分",
            "60分",
            "90分",
            "120分"
        )

        val spinnerAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                cookTimes
            )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        cookTimeSpinner.adapter =
            spinnerAdapter

// =========================
// 難易度
// =========================

        val difficulties =
            arrayOf(
                "簡単",
                "普通",
                "難しい"
            )

        difficultyAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                difficulties
            )

        difficultyAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        difficultySpinner.adapter =
            difficultyAdapter

        // =========================
        // 人数
        // =========================

        val servings =
            arrayOf(
                "1人分",
                "2人分",
                "3人分",
                "4人分",
                "5人分",
                "6人分",
                "7人分",
                "8人分"
            )

        val servingsAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                servings
            )

        servingsAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        servingsSpinner.adapter =
            servingsAdapter

        // =========================
        // 編集モード判定
        // =========================

        recipeId =
            intent.getIntExtra(
                "recipe_id",
                -1
            )

        isEditMode =
            recipeId != -1


        if (isEditMode) {

            // 編集画面
            findViewById<TextView>(
                R.id.screenTitle
            )?.text = "レシピ編集"

            loadRecipe()

        } else {

            // 新規追加
            findViewById<TextView>(
                R.id.screenTitle
            )?.text = "レシピ追加"
        }


        // =========================
        // 画像選択
        // =========================

        findViewById<Button>(
            R.id.selectImageButton
        ).setOnClickListener {

            imagePicker.launch(
                arrayOf("image/*")
            )
        }


        // =========================
        // 戻る
        // =========================

        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }


        // =========================
        // 保存
        // =========================

        findViewById<TextView>(
            R.id.saveButton
        ).setOnClickListener {

            saveRecipe()
        }
    }


    // =========================
    // レシピ読み込み
    // =========================

    private fun loadRecipe() {

        val recipe =
            dbHelper.getRecipeById(
                recipeId
            )

        if (recipe == null) {

            Toast.makeText(
                this,
                "レシピが見つかりません",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        // 名前
        nameEditText.setText(
            recipe.name
        )


        // 説明
        descriptionEditText.setText(
            recipe.description
        )


        // 調理時間
        val timeText =
            "${recipe.cookTime}分"

        val spinnerPosition =
            (cookTimeSpinner.adapter as ArrayAdapter<String>)
                .getPosition(timeText)

        if (spinnerPosition >= 0) {

            cookTimeSpinner.setSelection(
                spinnerPosition
            )
        }

        // 難易度
        val difficultyPosition =
            difficultyAdapter.getPosition(
                recipe.difficulty
            )

        if (difficultyPosition >= 0) {

            difficultySpinner.setSelection(
                difficultyPosition
            )
        }

        // 人数
        if (
            recipe.servings in 1..8
        ) {

            servingsSpinner.setSelection(
                recipe.servings - 1
            )
        }

        // 画像
        if (!recipe.imageUri.isNullOrEmpty()) {

            selectedImageUri =
                Uri.parse(
                    recipe.imageUri
                )

            recipeImage.setImageURI(
                selectedImageUri
            )
        }
    }


    // =========================
    // 保存
    // =========================

    private fun saveRecipe() {

        val name =
            nameEditText.text
                .toString()
                .trim()

        val description =
            descriptionEditText.text
                .toString()
                .trim()


        // 名前チェック
        if (name.isEmpty()) {

            nameEditText.error =
                "レシピ名を入力してください"

            return
        }


        // 調理時間
        val cookTimeText =
            cookTimeSpinner.selectedItem
                .toString()

        val cookTime =
            cookTimeText
                .replace("分", "")
                .toInt()

        // 難易度
        val difficulty =
            difficultySpinner.selectedItem
                .toString()

        // 人数
        val servings =
            servingsSpinner.selectedItemPosition + 1

        // 画像
        val imageUri =
            selectedImageUri?.toString()


        if (isEditMode) {

            // =========================
            // 更新
            // =========================

            val result =
                dbHelper.updateRecipe(
                    recipeId = recipeId,
                    name = name,
                    description = description,
                    cookTime = cookTime,
                    difficulty = difficulty,
                    servings = servings,
                    imageUri = imageUri
                )

            if (result > 0) {

                Toast.makeText(
                    this,
                    "レシピを更新しました",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "更新に失敗しました",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            // =========================
            // 新規追加
            // =========================

            val result =
                dbHelper.insertRecipe(
                    name = name,
                    description = description,
                    cookTime = cookTime,
                    difficulty = difficulty,
                    servings = servings,
                    imageUri = imageUri
                )

            if (result != -1L) {

                Toast.makeText(
                    this,
                    "レシピを保存しました",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "保存に失敗しました",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}