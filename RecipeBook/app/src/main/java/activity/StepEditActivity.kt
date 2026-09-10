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

class StepEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var timerSpinner: Spinner
    private lateinit var stepImage: ImageView
    private lateinit var stepNumberText: TextView

    private var recipeId: Int = -1
    private var selectedImageUri: Uri? = null

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

                stepImage.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_step_edit
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


        // View
        titleEditText =
            findViewById(
                R.id.titleEditText
            )

        descriptionEditText =
            findViewById(
                R.id.descriptionEditText
            )

        timerSpinner =
            findViewById(
                R.id.timerSpinner
            )

        stepImage =
            findViewById(
                R.id.stepImage
            )

        stepNumberText =
            findViewById(
                R.id.stepNumberText
            )


        // 手順番号
        val nextStepNumber =
            dbHelper.getNextStepNumber(
                recipeId
            )

        stepNumberText.text =
            "手順 $nextStepNumber"


        // タイマー
        val timerItems =
            arrayOf(
                "タイマーなし",
                "30秒",
                "1分",
                "2分",
                "3分",
                "5分",
                "10分",
                "15分",
                "20分",
                "30分",
                "60分"
            )

        val timerAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                timerItems
            )

        timerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        timerSpinner.adapter =
            timerAdapter


        // 画像選択
        findViewById<Button>(
            R.id.selectImageButton
        ).setOnClickListener {

            imagePicker.launch(
                arrayOf("image/*")
            )
        }


        // 戻る
        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }


        // 保存
        findViewById<Button>(
            R.id.saveStepButton
        ).setOnClickListener {

            saveStep()
        }
    }


    private fun saveStep() {

        val title =
            titleEditText.text
                .toString()
                .trim()

        val description =
            descriptionEditText.text
                .toString()
                .trim()


        if (title.isEmpty()) {

            titleEditText.error =
                "タイトルを入力してください"

            return
        }


        if (description.isEmpty()) {

            descriptionEditText.error =
                "調理手順を入力してください"

            return
        }


        // 次の手順番号
        val stepNumber =
            dbHelper.getNextStepNumber(
                recipeId
            )


        // タイマー秒数
        val timer =
            getSelectedTimerSeconds()


        // DB保存
        val result =
            dbHelper.insertStep(
                recipeId = recipeId,
                stepNumber = stepNumber,
                title = title,
                description = description,
                imageUri = selectedImageUri?.toString(),
                timer = timer
            )


        if (result != -1L) {

            Toast.makeText(
                this,
                "手順を保存しました",
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


    private fun getSelectedTimerSeconds(): Int {

        return when (
            timerSpinner.selectedItemPosition
        ) {

            0 -> 0
            1 -> 30
            2 -> 60
            3 -> 120
            4 -> 180
            5 -> 300
            6 -> 600
            7 -> 900
            8 -> 1200
            9 -> 1800
            10 -> 3600

            else -> 0
        }
    }
}