package com.example.recipebook

import android.content.Intent
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
import kotlin.jvm.java
import androidx.appcompat.app.AlertDialog

class TagActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var adapter: TagAdapter

    private lateinit var searchEditText: EditText

    private var tagList =
        mutableListOf<Tag>()

    private fun showDeleteDialog(
        tag: Tag
    ) {

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("タグを削除しますか？")
            .setMessage(
                "#${tag.name}を削除します。"
            )
            .setNegativeButton(
                "キャンセル",
                null
            )
            .setPositiveButton(
                "削除"
            ) { _, _ ->

                deleteTag(tag)
            }
            .show()
    }

    private fun deleteTag(
        tag: Tag
    ) {

        val result =
            dbHelper.deleteTag(
                tag.id
            )

        if (result) {

            Toast.makeText(
                this,
                "#${tag.name}を削除しました",
                Toast.LENGTH_SHORT
            ).show()

            loadTags()

        } else {

            Toast.makeText(
                this,
                "削除に失敗しました",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_tag
        )

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
            TagAdapter(
                tagList
            ) { tag ->

                showDeleteDialog(tag)
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


        // タグ追加
        findViewById<
                com.google.android.material.floatingactionbutton.FloatingActionButton
                >(R.id.addTagButton)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        TagEditActivity::class.java
                    )
                )
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
    }


    override fun onResume() {
        super.onResume()

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
}