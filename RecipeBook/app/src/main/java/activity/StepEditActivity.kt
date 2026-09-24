package com.example.recipebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

    private lateinit var timerMinuteEditText: EditText
    private lateinit var timerSecondEditText: EditText

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

        timerMinuteEditText =
            findViewById(
                R.id.timerMinuteEditText
            )

        timerSecondEditText =
            findViewById(
                R.id.timerSecondEditText
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

            // 新規の場合は0分0秒
            timerMinuteEditText.setText("0")
            timerSecondEditText.setText("0")
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


        // =========================
        // タイマー
        // =========================

        val minutes =
            step.timer / 60

        val seconds =
            step.timer % 60

        timerMinuteEditText.setText(
            minutes.toString()
        )

        timerSecondEditText.setText(
            seconds.toString()
        )
    }


    // =========================
    // タイマー秒数取得
    // =========================

    private fun getTimerSeconds(): Int? {

        val minuteText =
            timerMinuteEditText.text
                .toString()
                .trim()

        val secondText =
            timerSecondEditText.text
                .toString()
                .trim()


        // 空欄は0として扱う
        val minutes =
            if (minuteText.isEmpty()) {
                0
            } else {
                minuteText.toIntOrNull()
            }


        val seconds =
            if (secondText.isEmpty()) {
                0
            } else {
                secondText.toIntOrNull()
            }


        // 数字として入力できなかった
        if (minutes == null) {

            timerMinuteEditText.error =
                "数字を入力してください"

            return null
        }


        if (seconds == null) {

            timerSecondEditText.error =
                "数字を入力してください"

            return null
        }


        // マイナスチェック
        if (minutes < 0) {

            timerMinuteEditText.error =
                "0以上を入力してください"

            return null
        }


        // 秒は0～59
        if (seconds !in 0..59) {

            timerSecondEditText.error =
                "秒は0～59で入力してください"

            return null
        }


        // 両方0ならタイマーなし
        return minutes * 60 + seconds
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


        // タイマー
        val timer =
            getTimerSeconds()
                ?: return


        // 画像
        val imageUri =
            selectedImageUri?.toString()


        if (isEditMode) {

            // =========================
            // 更新
            // =========================

            val result =
                dbHelper.updateStep(
                    stepId = stepId,
                    title = title,
                    description = description,
                    imageUri = imageUri,
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
                    imageUri = imageUri,
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
            .setTitle(
                "手順を削除しますか？"
            )
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

            // 手順番号を振り直す
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