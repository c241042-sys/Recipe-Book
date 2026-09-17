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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.database.RecipeDBHelper

class StepEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var timerSpinner: Spinner
    private lateinit var stepImage: ImageView
    private lateinit var stepNumberText: TextView
    private lateinit var screenTitle: TextView
    private lateinit var deleteButton: Button

    private var recipeId: Int = -1
    private var stepId: Int = -1

    private var isEditMode = false

    private var selectedImageUri: Uri? = null

    // =========================
    // 画像選択
    // =========================
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

        dbHelper =
            RecipeDBHelper(this)


        // =========================
        // View取得
        // =========================

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

        screenTitle =
            findViewById(
                R.id.screenTitle
            )

        deleteButton =
            findViewById(
                R.id.deleteStepButton
            )


        // =========================
        // ID取得
        // =========================

        recipeId =
            intent.getIntExtra(
                "recipe_id",
                -1
            )

        stepId =
            intent.getIntExtra(
                "step_id",
                -1
            )

        isEditMode =
            stepId != -1


        if (recipeId == -1) {

            Toast.makeText(
                this,
                "レシピIDを取得できませんでした",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        // =========================
        // タイマー
        // =========================

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


        // =========================
        // 新規追加 / 編集
        // =========================

        if (isEditMode) {

            screenTitle.text =
                "手順編集"

            deleteButton.visibility =
                Button.VISIBLE

            loadStep()

        } else {

            screenTitle.text =
                "手順追加"

            deleteButton.visibility =
                Button.GONE

            val nextNumber =
                dbHelper.getNextStepNumber(
                    recipeId
                )

            stepNumberText.text =
                "手順 $nextNumber"
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

        findViewById<Button>(
            R.id.saveStepButton
        ).setOnClickListener {

            saveStep()
        }


        // =========================
        // 削除
        // =========================

        deleteButton.setOnClickListener {

            showDeleteDialog()
        }
    }


    // =========================
    // 手順読み込み
    // =========================

    private fun loadStep() {

        val steps =
            dbHelper.getStepsByRecipeId(
                recipeId
            )

        val step =
            steps.find {
                it.id == stepId
            }

        if (step == null) {

            Toast.makeText(
                this,
                "手順が見つかりません",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        // 手順番号
        stepNumberText.text =
            "手順 ${step.stepNumber}"


        // タイトル
        titleEditText.setText(
            step.title
        )


        // 説明
        descriptionEditText.setText(
            step.description
        )


        // 画像
        if (!step.imageUri.isNullOrEmpty()) {

            selectedImageUri =
                Uri.parse(
                    step.imageUri
                )

            stepImage.setImageURI(
                selectedImageUri
            )
        }


        // タイマー
        setTimerSpinner(
            step.timer
        )
    }


    // =========================
    // タイマーSpinner設定
    // =========================

    private fun setTimerSpinner(
        timerSeconds: Int
    ) {

        val position =
            when (timerSeconds) {

                0 -> 0
                30 -> 1
                60 -> 2
                120 -> 3
                180 -> 4
                300 -> 5
                600 -> 6
                900 -> 7
                1200 -> 8
                1800 -> 9
                3600 -> 10

                else -> 0
            }

        timerSpinner.setSelection(
            position
        )
    }


    // =========================
    // タイマー秒数取得
    // =========================

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


    // =========================
    // 保存
    // =========================

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


        val timer =
            getSelectedTimerSeconds()


        if (isEditMode) {

            // =========================
            // 更新
            // =========================

            val result =
                dbHelper.updateStep(
                    stepId = stepId,
                    title = title,
                    description = description,
                    imageUri =
                        selectedImageUri?.toString(),
                    timer = timer
                )

            if (result > 0) {

                Toast.makeText(
                    this,
                    "手順を更新しました",
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

            val stepNumber =
                dbHelper.getNextStepNumber(
                    recipeId
                )

            val result =
                dbHelper.insertStep(
                    recipeId = recipeId,
                    stepNumber = stepNumber,
                    title = title,
                    description = description,
                    imageUri =
                        selectedImageUri?.toString(),
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
    }


    // =========================
    // 削除確認
    // =========================

    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
            .setTitle("手順を削除しますか？")
            .setMessage(
                "この手順を削除します。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "削除"
            ) { _, _ ->

                deleteStep()
            }
            .show()
    }


    // =========================
    // 削除
    // =========================

    private fun deleteStep() {

        val result =
            dbHelper.deleteStep(
                stepId
            )

        if (result) {

            // 番号を詰め直す
            dbHelper.reorderSteps(
                recipeId
            )

            Toast.makeText(
                this,
                "手順を削除しました",
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