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
    private lateinit var recipeImage: ImageView

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