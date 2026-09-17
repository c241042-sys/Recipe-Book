package com.example.recipebook

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.database.RecipeDBHelper

class CookingHistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: RecipeDBHelper

    private lateinit var adapter: CookingHistoryAdapter

    private lateinit var emptyText: TextView

    private var historyList =
        mutableListOf<CookingHistory>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_cooking_history
        )

        dbHelper =
            RecipeDBHelper(this)

        emptyText =
            findViewById(
                R.id.emptyText
            )

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.historyRecyclerView
            )


        // Adapter
        adapter =
            CookingHistoryAdapter(
                historyList
            )

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
    }


    override fun onResume() {
        super.onResume()

        loadHistories()
    }


    private fun loadHistories() {

        historyList =
            dbHelper.getAllCookingHistories()

        adapter.updateList(
            historyList
        )


        if (historyList.isEmpty()) {

            emptyText.visibility =
                TextView.VISIBLE

        } else {

            emptyText.visibility =
                TextView.GONE
        }
    }
}