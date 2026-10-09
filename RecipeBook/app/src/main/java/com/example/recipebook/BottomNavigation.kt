package com.example.recipebook

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

object BottomNavigation {

    enum class Tab {
        HOME,
        FAVORITES,
        INGREDIENTS,
        TAGS,
        HISTORY
    }

    private data class MenuItem(
        val viewId: Int,
        val tab: Tab,
        val destination: Class<out Activity>
    )

    fun setup(
        activity: AppCompatActivity,
        selected: Tab
    ) {
        val items = listOf(
            MenuItem(
                R.id.homeButton,
                Tab.HOME,
                MainActivity::class.java
            ),
            MenuItem(
                R.id.favoriteManageButton,
                Tab.FAVORITES,
                FavoriteActivity::class.java
            ),
            MenuItem(
                R.id.ingredientManageButton,
                Tab.INGREDIENTS,
                IngredientActivity::class.java
            ),
            MenuItem(
                R.id.tagManageButton,
                Tab.TAGS,
                TagActivity::class.java
            ),
            MenuItem(
                R.id.historyButton,
                Tab.HISTORY,
                CookingHistoryActivity::class.java
            )
        )

        items.forEach { item ->

            // クリック時の画面切り替え
            activity.findViewById<View>(item.viewId)
                ?.setOnClickListener {

                    if (item.tab == selected) {
                        return@setOnClickListener
                    }

                    val intent = Intent(
                        activity,
                        item.destination
                    ).apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                        )
                    }

                    activity.startActivity(intent)
                }

            // 選択中のメニューをオレンジ色にする
            val menuView =
                activity.findViewById<LinearLayout>(
                    item.viewId
                )

            val color = if (item.tab == selected) {
                Color.parseColor("#E88423")
            } else {
                Color.parseColor("#666666")
            }

            menuView?.let {
                (it.getChildAt(0) as? TextView)
                    ?.setTextColor(color)

                (it.getChildAt(1) as? TextView)
                    ?.setTextColor(color)
            }
        }
    }
}