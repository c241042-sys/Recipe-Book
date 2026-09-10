package com.example.recipebook

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val cookTime: Int,
    val imageUri: String?,
    val tags: List<String> = emptyList()
)