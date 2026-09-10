package activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.R
import com.example.recipebook.database.RecipeDBHelper

class IngredientEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var nameEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var memoEditText: EditText

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
    }

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