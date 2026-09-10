package com.example.recipebook

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class AddRecipeTagActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var adapter: RecipeTagAdapter
    private lateinit var searchEditText: EditText

    private var recipeId: Int = -1

    private var tagList =
        mutableListOf<Tag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_add_recipe_tag
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

        searchEditText =
            findViewById(
                R.id.searchTagEditText
            )

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.tagRecyclerView
            )


        adapter =
            RecipeTagAdapter(
                tagList
            ) { tag ->

                addTag(tag)
            }

        recyclerView.adapter =
            adapter

        recyclerView.layoutManager =
            LinearLayoutManager(this)


        // 戻る
        findViewById<TextView>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }


        // 検索
        searchEditText.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    searchTag(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        loadTags()
    }


    private fun loadTags() {

        tagList =
            dbHelper.getAllTags()

        adapter.updateList(
            tagList
        )
    }


    private fun searchTag(
        keyword: String
    ) {

        val text =
            keyword.trim()

        if (text.isEmpty()) {

            adapter.updateList(
                tagList
            )

            return
        }

        val result =
            tagList.filter { tag ->

                tag.name.contains(
                    text,
                    ignoreCase = true
                )
            }

        adapter.updateList(result)
    }


    private fun addTag(
        tag: Tag
    ) {

        if (
            dbHelper.isTagAddedToRecipe(
                recipeId,
                tag.id
            )
        ) {

            Toast.makeText(
                this,
                "このタグはすでに登録されています",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val result =
            dbHelper.addTagToRecipe(
                recipeId,
                tag.id
            )


        if (result != -1L) {

            Toast.makeText(
                this,
                "#${tag.name}を追加しました",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "追加に失敗しました",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}