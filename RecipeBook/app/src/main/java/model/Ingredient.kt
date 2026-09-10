package com.example.recipebook

data class Ingredient(
    val id: Int,
    val name: String,
    val category: String,
    val memo: String,
    val favorite: Boolean = false
)