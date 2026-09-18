package com.example.recipebook

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val cookTime: Int,
    val imageUri: String?,
    val favorite: Boolean = false,
    val tags: List<String> = emptyList(),

    // 追加
    val difficulty: String = "普通",
    val servings: Int = 2
)