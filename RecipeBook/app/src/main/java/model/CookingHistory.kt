package com.example.recipebook

data class CookingHistory(
    val id: Int,
    val recipeId: Int,
    val recipeName: String,
    val startTime: String,
    val finishTime: String,
    val elapsedTime: Int,
    val comment: String
)