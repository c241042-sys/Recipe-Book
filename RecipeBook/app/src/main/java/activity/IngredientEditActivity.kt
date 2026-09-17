package com.example.recipebook

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.database.RecipeDBHelper

class IngredientEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var nameEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var memoEditText: EditText

    private var ingredientId: Int = -1

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_ingredient_edit
        )

        dbHelper =
            RecipeDBHelper(this)

        nameEditText =
            findViewById(
                R.id.ingredientNameEditText
            )

        categoryEditText =
            findViewById(
                R.id.categoryEditText
            )

        memoEditText =
            findViewById(
                R.id.memoEditText
            )


        // 編集ID取得
        ingredientId =
            intent.getIntExtra(
                "ingredient_id",
                -1
            )

        isEditMode =
            ingredientId != -1


        // タイトル変更
        findViewById<TextView>(
            R.id.screenTitle
        ).text =
            if (isEditMode) {
                "材料編集"
            } else {
                "材料追加"
            }


        // 編集の場合
        if (isEditMode) {

            loadIngredient()
        }


        // 戻る
        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }


        // 保存
        findViewById<Button>(
            R.id.saveIngredientButton
        ).setOnClickListener {

            saveIngredient()
        }


        // 編集時だけ削除ボタン表示
        val deleteButton =
            findViewById<Button>(
                R.id.deleteIngredientButton
            )

        if (isEditMode) {

            deleteButton.visibility =
                Button.VISIBLE

            deleteButton.setOnClickListener {

                showDeleteDialog()
            }

        } else {

            deleteButton.visibility =
                Button.GONE
        }
    }


    // =========================
    // 材料読み込み
    // =========================
    private fun loadIngredient() {

        val ingredients =
            dbHelper.getAllIngredients()

        val ingredient =
            ingredients.find {
                it.id == ingredientId
            }

        if (ingredient == null) {

            Toast.makeText(
                this,
                "材料が見つかりません",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        nameEditText.setText(
            ingredient.name
        )

        categoryEditText.setText(
            ingredient.category
        )

        memoEditText.setText(
            ingredient.memo
        )
    }


    // =========================
    // 保存
    // =========================
    private fun saveIngredient() {

        val name =
            nameEditText.text
                .toString()
                .trim()

        val category =
            categoryEditText.text
                .toString()
                .trim()

        val memo =
            memoEditText.text
                .toString()
                .trim()


        if (name.isEmpty()) {

            nameEditText.error =
                "材料名を入力してください"

            return
        }


        if (isEditMode) {

            val result =
                dbHelper.updateIngredient(
                    ingredientId = ingredientId,
                    name = name,
                    category = category,
                    memo = memo
                )

            if (result > 0) {

                Toast.makeText(
                    this,
                    "材料を更新しました",
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

            val result =
                dbHelper.insertIngredient(
                    name = name,
                    category = category,
                    memo = memo
                )

            if (result != -1L) {

                Toast.makeText(
                    this,
                    "材料を保存しました",
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


    // =========================
    // 削除確認
    // =========================
    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
            .setTitle("材料を削除しますか？")
            .setMessage(
                "この材料を使用しているレシピとの関連付けも削除されます。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "削除"
            ) { _, _ ->

                deleteIngredient()
            }
            .show()
    }


    // =========================
    // 削除
    // =========================
    private fun deleteIngredient() {

        val result =
            dbHelper.deleteIngredient(
                ingredientId
            )

        if (result) {

            Toast.makeText(
                this,
                "材料を削除しました",
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
}