package com.example.recipebook

data class Step(
    val id: Int,
    val recipeId: Int,
    val stepNumber: Int,
    val title: String,
    val description: String,
    val imageUri: String?,
    val timer: Int
)