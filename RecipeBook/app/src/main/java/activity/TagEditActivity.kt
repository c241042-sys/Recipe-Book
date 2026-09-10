package com.example.recipebook

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipebook.database.RecipeDBHelper

class TagEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var tagNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_tag_edit
        )

        dbHelper =
            RecipeDBHelper(this)

        tagNameEditText =
            findViewById(
                R.id.tagNameEditText
            )


        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }


        findViewById<Button>(
            R.id.saveTagButton
        ).setOnClickListener {

            saveTag()
        }
    }


    private fun saveTag() {

        val name =
            tagNameEditText.text
                .toString()
                .trim()

        if (name.isEmpty()) {

            tagNameEditText.error =
                "タグ名を入力してください"

            return
        }


        val result =
            dbHelper.insertTag(name)


        if (result != -1L) {

            Toast.makeText(
                this,
                "タグを保存しました",
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