package com.example.recipebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.database.RecipeDBHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CookingActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var stepCounter: TextView
    private lateinit var stepImage: ImageView
    private lateinit var stepTitle: TextView
    private lateinit var stepDescription: TextView

    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var startTimerButton: Button
    private lateinit var resetTimerButton: Button

    private var recipeId: Int = -1

    private var steps =
        mutableListOf<Step>()

    private var currentStepIndex = 0

    private var countDownTimer: CountDownTimer? = null

    private var remainingTime: Long = 0

    private var timerRunning = false

    private var cookingStartTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_cooking
        )

        // レシピID取得
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
        stepCounter =
            findViewById(R.id.stepCounter)

        stepImage =
            findViewById(R.id.stepImage)

        stepTitle =
            findViewById(R.id.stepTitle)

        stepDescription =
            findViewById(R.id.stepDescription)

        timerText =
            findViewById(R.id.timerText)

        progressBar =
            findViewById(R.id.progressBar)

        progressText =
            findViewById(R.id.progressText)

        previousButton =
            findViewById(R.id.previousButton)

        nextButton =
            findViewById(R.id.nextButton)

        startTimerButton =
            findViewById(R.id.startTimerButton)

        resetTimerButton =
            findViewById(R.id.resetTimerButton)


        // 調理開始時間
        cookingStartTime =
            getCurrentTime()


        // 手順取得
        steps =
            dbHelper.getStepsByRecipeId(
                recipeId
            )


        // 手順がない場合
        if (steps.isEmpty()) {

            Toast.makeText(
                this,
                "作り方が登録されていません",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        // 初期表示
        showCurrentStep()


        // 戻る
        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            showExitDialog()
        }


        // 終了
        findViewById<TextView>(
            R.id.exitButton
        ).setOnClickListener {

            showExitDialog()
        }


        // 前へ
        previousButton.setOnClickListener {

            previousStep()
        }


        // 次へ
        nextButton.setOnClickListener {

            nextStep()
        }


        // タイマー開始
        startTimerButton.setOnClickListener {

            startTimer()
        }


        // タイマーリセット
        resetTimerButton.setOnClickListener {

            resetTimer()
        }
    }


    // =========================
    // 現在の手順を表示
    // =========================
    private fun showCurrentStep() {

        val step =
            steps[currentStepIndex]


        // 手順番号
        stepCounter.text =
            "手順 ${currentStepIndex + 1} / ${steps.size}"


        // タイトル
        stepTitle.text =
            step.title


        // 説明
        stepDescription.text =
            step.description


        // 画像
        if (!step.imageUri.isNullOrEmpty()) {

            stepImage.setImageURI(
                Uri.parse(step.imageUri)
            )

        } else {

            stepImage.setImageResource(
                R.drawable.recipe_placeholder
            )
        }


        // 進捗
        val progress =
            ((currentStepIndex + 1).toFloat()
                    / steps.size.toFloat()
                    * 100)
                .toInt()

        progressBar.progress =
            progress

        progressText.text =
            "$progress%"


        // 前へボタン
        previousButton.isEnabled =
            currentStepIndex > 0


        // 最後の手順
        if (
            currentStepIndex ==
            steps.size - 1
        ) {

            nextButton.text =
                "調理完了"

        } else {

            nextButton.text =
                "次へ →"
        }


        // タイマー停止
        stopTimer()


        // 手順のタイマー設定
        remainingTime =
            step.timer.toLong()

        updateTimerText()

        if (step.timer > 0) {

            startTimerButton.isEnabled = true
            resetTimerButton.isEnabled = true

        } else {

            timerText.text =
                "タイマーなし"

            startTimerButton.isEnabled = false
            resetTimerButton.isEnabled = false
        }
    }


    // =========================
    // 前の手順
    // =========================
    private fun previousStep() {

        if (currentStepIndex <= 0) {
            return
        }

        currentStepIndex--

        showCurrentStep()
    }


    // =========================
    // 次の手順
    // =========================
    private fun nextStep() {

        if (
            currentStepIndex <
            steps.size - 1
        ) {

            currentStepIndex++

            showCurrentStep()

        } else {

            finishCooking()
        }
    }


    // =========================
    // タイマー開始
    // =========================
    private fun startTimer() {

        if (
            remainingTime <= 0 ||
            timerRunning
        ) {
            return
        }


        timerRunning = true

        startTimerButton.text =
            "実行中"

        startTimerButton.isEnabled =
            false


        countDownTimer =
            object : CountDownTimer(
                remainingTime * 1000,
                1000
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    remainingTime =
                        millisUntilFinished / 1000

                    updateTimerText()
                }


                override fun onFinish() {

                    remainingTime = 0

                    timerRunning = false

                    updateTimerText()

                    startTimerButton.text =
                        "開始"

                    startTimerButton.isEnabled =
                        false

                    Toast.makeText(
                        this@CookingActivity,
                        "タイマー終了！",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.start()
    }


    // =========================
    // タイマー停止
    // =========================
    private fun stopTimer() {

        countDownTimer?.cancel()

        countDownTimer =
            null

        timerRunning =
            false

        startTimerButton.text =
            "開始"

        startTimerButton.isEnabled =
            remainingTime > 0
    }


    // =========================
    // タイマーリセット
    // =========================
    private fun resetTimer() {

        stopTimer()

        remainingTime =
            steps[currentStepIndex]
                .timer
                .toLong()

        updateTimerText()

        startTimerButton.isEnabled =
            remainingTime > 0
    }


    // =========================
    // タイマー表示
    // =========================
    private fun updateTimerText() {

        if (remainingTime <= 0) {

            timerText.text =
                "00:00"

            return
        }


        val minutes =
            remainingTime / 60

        val seconds =
            remainingTime % 60


        timerText.text =
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
            )
    }


    // =========================
    // 調理完了
    // =========================
    private fun finishCooking() {

        stopTimer()


        val finishTime =
            getCurrentTime()


        val elapsedTime =
            calculateElapsedTime()


        val result =
            dbHelper.insertCookingHistory(
                recipeId = recipeId,
                startTime = cookingStartTime,
                finishTime = finishTime,
                elapsedTime = elapsedTime,
                comment = ""
            )


        if (result != -1L) {

            Toast.makeText(
                this,
                "調理完了！",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            Toast.makeText(
                this,
                "調理履歴の保存に失敗しました",
                Toast.LENGTH_SHORT
            ).show()
        }


        finish()
    }


    // =========================
    // 終了確認
    // =========================
    private fun showExitDialog() {

        AlertDialog.Builder(this)
            .setTitle("調理を終了しますか？")
            .setMessage(
                "現在の調理は完了せずに終了します。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "終了"
            ) { _, _ ->

                stopTimer()

                finish()
            }
            .show()
    }


    // =========================
    // 現在時刻
    // =========================
    private fun getCurrentTime(): String {

        val format =
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )

        return format.format(
            Date()
        )
    }


    // =========================
    // 経過時間
    // =========================
    private fun calculateElapsedTime(): Int {

        return try {

            val format =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                )

            val start =
                format.parse(
                    cookingStartTime
                ) ?: return 0

            val end =
                Date()

            (
                    (end.time - start.time)
                            / 1000
                    ).toInt()

        } catch (e: Exception) {

            0
        }
    }


    override fun onDestroy() {

        countDownTimer?.cancel()

        super.onDestroy()
    }
}