package com.example.recipebook.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.recipebook.CookingHistory
import com.example.recipebook.Ingredient
import com.example.recipebook.Recipe
import com.example.recipebook.RecipeIngredient
import com.example.recipebook.RecipeTag
import com.example.recipebook.Step
import com.example.recipebook.Tag

class RecipeDBHelper(context: Context) :
    SQLiteOpenHelper(context, "RecipeBook.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        // レシピ
        db.execSQL("""
            CREATE TABLE recipes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                cook_time INTEGER,
                image_uri TEXT,
                favorite INTEGER DEFAULT 0
            )
        """.trimIndent())

        // 材料
        db.execSQL("""
            CREATE TABLE ingredients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT,
                memo TEXT,
                favorite INTEGER DEFAULT 0
            )
        """.trimIndent())

        // レシピと材料
        db.execSQL("""
            CREATE TABLE recipe_ingredients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recipe_id INTEGER NOT NULL,
                ingredient_id INTEGER NOT NULL,
                amount TEXT,
                display_order INTEGER
            )
        """.trimIndent())

        // タグ
        db.execSQL("""
            CREATE TABLE tags (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            )
        """.trimIndent())

        // レシピとタグ
        db.execSQL("""
            CREATE TABLE recipe_tags (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recipe_id INTEGER NOT NULL,
                tag_id INTEGER NOT NULL
            )
        """.trimIndent())

        // 手順
        db.execSQL("""
            CREATE TABLE steps (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recipe_id INTEGER NOT NULL,
                step_number INTEGER NOT NULL,
                title TEXT,
                description TEXT,
                image_uri TEXT,
                timer INTEGER DEFAULT 0
            )
        """.trimIndent())

        // 調理履歴
        db.execSQL("""
            CREATE TABLE cooking_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recipe_id INTEGER NOT NULL,
                start_time TEXT,
                finish_time TEXT,
                elapsed_time INTEGER,
                comment TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS cooking_history")
        db.execSQL("DROP TABLE IF EXISTS steps")
        db.execSQL("DROP TABLE IF EXISTS recipe_tags")
        db.execSQL("DROP TABLE IF EXISTS tags")
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients")
        db.execSQL("DROP TABLE IF EXISTS ingredients")
        db.execSQL("DROP TABLE IF EXISTS recipes")

        onCreate(db)
    }

    // =========================
    // レシピ追加
    // =========================
    fun insertRecipe(
        name: String,
        description: String,
        cookTime: Int,
        imageUri: String?
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("description", description)
            put("cook_time", cookTime)

            if (imageUri != null) {
                put("image_uri", imageUri)
            }
        }

        return db.insert("recipes", null, values)
    }

    // =========================
    // レシピ一覧取得
    // =========================
    fun getAllRecipes(): MutableList<Recipe> {

        val recipeList = mutableListOf<Recipe>()

        val db = readableDatabase

        val cursor = db.query(
            "recipes",
            null,
            null,
            null,
            null,
            null,
            "id DESC"
        )

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(
                        it.getColumnIndexOrThrow("id")
                    )

                val name =
                    it.getString(
                        it.getColumnIndexOrThrow("name")
                    )

                val description =
                    it.getString(
                        it.getColumnIndexOrThrow("description")
                    ) ?: ""

                val cookTime =
                    it.getInt(
                        it.getColumnIndexOrThrow("cook_time")
                    )

                val imageUri =
                    it.getString(
                        it.getColumnIndexOrThrow("image_uri")
                    )

                val favorite =
                    it.getInt(
                        it.getColumnIndexOrThrow("favorite")
                    ) == 1

                // レシピのタグを取得
                val tags =
                    getTagsByRecipeId(id)
                        .map { tag -> tag.name }

                recipeList.add(
                    Recipe(
                        id = id,
                        name = name,
                        description = description,
                        cookTime = cookTime,
                        imageUri = imageUri,
                        favorite = favorite,
                        tags = tags
                    )
                )
            }
        }

        return recipeList
    }

    // =========================
    // レシピ1件取得
    // =========================
    fun getRecipeById(recipeId: Int): Recipe? {

        val db = readableDatabase

        val cursor = db.query(
            "recipes",
            null,
            "id = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                val id =
                    it.getInt(
                        it.getColumnIndexOrThrow("id")
                    )

                val name =
                    it.getString(
                        it.getColumnIndexOrThrow("name")
                    )

                val description =
                    it.getString(
                        it.getColumnIndexOrThrow("description")
                    ) ?: ""

                val cookTime =
                    it.getInt(
                        it.getColumnIndexOrThrow("cook_time")
                    )

                val imageUri =
                    it.getString(
                        it.getColumnIndexOrThrow("image_uri")
                    )

                val favorite =
                    it.getInt(
                        it.getColumnIndexOrThrow("favorite")
                    ) == 1

                return Recipe(
                    id = id,
                    name = name,
                    description = description,
                    cookTime = cookTime,
                    imageUri = imageUri,
                    favorite = favorite
                )
            }
        }

        return null
    }

    // =========================
    // 材料追加
    // =========================
    fun insertIngredient(
        name: String,
        category: String,
        memo: String
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("memo", memo)
            put("favorite", 0)
        }

        return db.insert(
            "ingredients",
            null,
            values
        )
    }

    // =========================
    // 材料更新
    // =========================
    fun updateIngredient(
        ingredientId: Int,
        name: String,
        category: String,
        memo: String
    ): Int {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("memo", memo)
        }

        return db.update(
            "ingredients",
            values,
            "id = ?",
            arrayOf(ingredientId.toString())
        )
    }

    // =========================
    // 材料削除
    // =========================
    fun deleteIngredient(
        ingredientId: Int
    ): Boolean {

        val db = writableDatabase

        db.beginTransaction()

        try {

            // レシピとの関連付けを削除
            db.delete(
                "recipe_ingredients",
                "ingredient_id = ?",
                arrayOf(ingredientId.toString())
            )

            // 材料本体を削除
            val result =
                db.delete(
                    "ingredients",
                    "id = ?",
                    arrayOf(ingredientId.toString())
                )

            db.setTransactionSuccessful()

            return result > 0

        } finally {

            db.endTransaction()
        }
    }

    // =========================
    // レシピに材料を追加
    // =========================
    fun addIngredientToRecipe(
        recipeId: Int,
        ingredientId: Int,
        amount: String,
        displayOrder: Int
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("recipe_id", recipeId)
            put("ingredient_id", ingredientId)
            put("amount", amount)
            put("display_order", displayOrder)
        }

        return db.insert(
            "recipe_ingredients",
            null,
            values
        )
    }

    // =========================
    // 材料一覧取得
    // =========================
    fun getAllIngredients(): MutableList<Ingredient> {

        val ingredientList = mutableListOf<Ingredient>()

        val db = readableDatabase

        val cursor = db.query(
            "ingredients",
            null,
            null,
            null,
            null,
            null,
            "name ASC"
        )

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(
                        it.getColumnIndexOrThrow("id")
                    )

                val name =
                    it.getString(
                        it.getColumnIndexOrThrow("name")
                    )

                val category =
                    it.getString(
                        it.getColumnIndexOrThrow("category")
                    ) ?: ""

                val memo =
                    it.getString(
                        it.getColumnIndexOrThrow("memo")
                    ) ?: ""

                val favorite =
                    it.getInt(
                        it.getColumnIndexOrThrow("favorite")
                    ) == 1

                ingredientList.add(
                    Ingredient(
                        id = id,
                        name = name,
                        category = category,
                        memo = memo,
                        favorite = favorite
                    )
                )
            }
        }

        return ingredientList
    }

    // =========================
    // レシピに登録された材料取得
    // =========================
    fun getIngredientsByRecipeId(
        recipeId: Int
    ): MutableList<RecipeIngredient> {

        val ingredientList =
            mutableListOf<RecipeIngredient>()

        val db = readableDatabase

        val query = """
        SELECT
            ingredients.id,
            ingredients.name,
            recipe_ingredients.amount
        FROM recipe_ingredients
        INNER JOIN ingredients
            ON recipe_ingredients.ingredient_id = ingredients.id
        WHERE recipe_ingredients.recipe_id = ?
        ORDER BY recipe_ingredients.display_order ASC
    """.trimIndent()

        val cursor =
            db.rawQuery(
                query,
                arrayOf(recipeId.toString())
            )

        cursor.use {

            while (it.moveToNext()) {

                val ingredientId =
                    it.getInt(0)

                val name =
                    it.getString(1)

                val amount =
                    it.getString(2) ?: ""

                ingredientList.add(
                    RecipeIngredient(
                        ingredientId = ingredientId,
                        ingredientName = name,
                        amount = amount
                    )
                )
            }
        }

        return ingredientList
    }

    // =========================
    // レシピに材料が登録済みか確認
    // =========================
    fun isIngredientAddedToRecipe(
        recipeId: Int,
        ingredientId: Int
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            "recipe_ingredients",
            arrayOf("id"),
            "recipe_id = ? AND ingredient_id = ?",
            arrayOf(
                recipeId.toString(),
                ingredientId.toString()
            ),
            null,
            null,
            null
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    // =========================
    // レシピ材料の分量更新
    // =========================
    fun updateRecipeIngredientAmount(
        recipeId: Int,
        ingredientId: Int,
        amount: String
    ): Int {

        val db = writableDatabase

        val values =
            ContentValues().apply {

                put(
                    "amount",
                    amount
                )
            }

        return db.update(
            "recipe_ingredients",
            values,
            "recipe_id = ? AND ingredient_id = ?",
            arrayOf(
                recipeId.toString(),
                ingredientId.toString()
            )
        )
    }

    // =========================
    // タグ追加
    // =========================
    fun insertTag(
        name: String
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
        }

        return db.insert(
            "tags",
            null,
            values
        )
    }

    // =========================
    // タグ削除
    // =========================
    fun deleteTag(
        tagId: Int
    ): Boolean {

        val db = writableDatabase

        db.beginTransaction()

        try {

            // レシピとの関連付け削除
            db.delete(
                "recipe_tags",
                "tag_id = ?",
                arrayOf(tagId.toString())
            )

            // タグ本体削除
            val result =
                db.delete(
                    "tags",
                    "id = ?",
                    arrayOf(tagId.toString())
                )

            db.setTransactionSuccessful()

            return result > 0

        } finally {

            db.endTransaction()
        }
    }

    // =========================
    // タグ一覧取得
    // =========================
    fun getAllTags(): MutableList<Tag> {

        val tagList =
            mutableListOf<Tag>()

        val db = readableDatabase

        val cursor = db.query(
            "tags",
            null,
            null,
            null,
            null,
            null,
            "name ASC"
        )

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(
                        it.getColumnIndexOrThrow("id")
                    )

                val name =
                    it.getString(
                        it.getColumnIndexOrThrow("name")
                    )

                tagList.add(
                    Tag(
                        id = id,
                        name = name
                    )
                )
            }
        }

        return tagList
    }

    // =========================
    // レシピにタグを追加
    // =========================
    fun addTagToRecipe(
        recipeId: Int,
        tagId: Int
    ): Long {

        val db =
            writableDatabase

        val values =
            ContentValues().apply {

                put(
                    "recipe_id",
                    recipeId
                )

                put(
                    "tag_id",
                    tagId
                )
            }

        return db.insert(
            "recipe_tags",
            null,
            values
        )
    }

    // =========================
    // レシピにタグが登録済みか確認
    // =========================
    fun isTagAddedToRecipe(
        recipeId: Int,
        tagId: Int
    ): Boolean {

        val db =
            readableDatabase

        val cursor =
            db.query(
                "recipe_tags",
                arrayOf("id"),
                "recipe_id = ? AND tag_id = ?",
                arrayOf(
                    recipeId.toString(),
                    tagId.toString()
                ),
                null,
                null,
                null
            )

        cursor.use {

            return it.moveToFirst()
        }
    }

    // =========================
    // レシピのタグ一覧取得
    // =========================
    fun getTagsByRecipeId(
        recipeId: Int
    ): MutableList<RecipeTag> {

        val tagList =
            mutableListOf<RecipeTag>()

        val db =
            readableDatabase

        val query = """
        SELECT
            tags.id,
            tags.name
        FROM recipe_tags
        INNER JOIN tags
            ON recipe_tags.tag_id = tags.id
        WHERE recipe_tags.recipe_id = ?
        ORDER BY tags.name ASC
    """.trimIndent()

        val cursor =
            db.rawQuery(
                query,
                arrayOf(recipeId.toString())
            )

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(0)

                val name =
                    it.getString(1)

                tagList.add(
                    RecipeTag(
                        id = id,
                        name = name
                    )
                )
            }
        }

        return tagList
    }

    // =========================
    // レシピからタグを削除
    // =========================
    fun deleteTagFromRecipe(
        recipeId: Int,
        tagId: Int
    ): Int {

        val db = writableDatabase

        return db.delete(
            "recipe_tags",
            "recipe_id = ? AND tag_id = ?",
            arrayOf(
                recipeId.toString(),
                tagId.toString()
            )
        )
    }

    // =========================
    // 手順追加
    // =========================
    fun insertStep(
        recipeId: Int,
        stepNumber: Int,
        title: String,
        description: String,
        imageUri: String?,
        timer: Int
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("recipe_id", recipeId)
            put("step_number", stepNumber)
            put("title", title)
            put("description", description)

            if (imageUri != null) {
                put("image_uri", imageUri)
            }

            put("timer", timer)
        }

        return db.insert(
            "steps",
            null,
            values
        )
    }

    // =========================
    // 手順更新
    // =========================
    fun updateStep(
        stepId: Int,
        title: String,
        description: String,
        imageUri: String?,
        timer: Int
    ): Int {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("title", title)
            put("description", description)

            if (imageUri != null) {
                put("image_uri", imageUri)
            } else {
                putNull("image_uri")
            }

            put("timer", timer)
        }

        return db.update(
            "steps",
            values,
            "id = ?",
            arrayOf(stepId.toString())
        )
    }

    // =========================
    // 手順削除
    // =========================
    fun deleteStep(
        stepId: Int
    ): Boolean {

        val db = writableDatabase

        val result =
            db.delete(
                "steps",
                "id = ?",
                arrayOf(stepId.toString())
            )

        return result > 0
    }

    // =========================
    // レシピの手順一覧取得
    // =========================
    fun getStepsByRecipeId(
        recipeId: Int
    ): MutableList<Step> {

        val stepList =
            mutableListOf<Step>()

        val db = readableDatabase

        val cursor = db.query(
            "steps",
            null,
            "recipe_id = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            "step_number ASC"
        )

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(
                        it.getColumnIndexOrThrow("id")
                    )

                val recipeIdValue =
                    it.getInt(
                        it.getColumnIndexOrThrow("recipe_id")
                    )

                val stepNumber =
                    it.getInt(
                        it.getColumnIndexOrThrow("step_number")
                    )

                val title =
                    it.getString(
                        it.getColumnIndexOrThrow("title")
                    ) ?: ""

                val description =
                    it.getString(
                        it.getColumnIndexOrThrow("description")
                    ) ?: ""

                val imageUri =
                    it.getString(
                        it.getColumnIndexOrThrow("image_uri")
                    )

                val timer =
                    it.getInt(
                        it.getColumnIndexOrThrow("timer")
                    )

                stepList.add(
                    Step(
                        id = id,
                        recipeId = recipeIdValue,
                        stepNumber = stepNumber,
                        title = title,
                        description = description,
                        imageUri = imageUri,
                        timer = timer
                    )
                )
            }
        }

        return stepList
    }

    // =========================
    // 次の手順番号
    // =========================
    fun getNextStepNumber(
        recipeId: Int
    ): Int {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT MAX(step_number)
        FROM steps
        WHERE recipe_id = ?
        """.trimIndent(),
            arrayOf(recipeId.toString())
        )

        cursor.use {

            if (it.moveToFirst()) {

                val maxNumber =
                    it.getInt(0)

                return maxNumber + 1
            }
        }

        return 1
    }

    // =========================
    // 手順番号を振り直す
    // =========================
    fun reorderSteps(
        recipeId: Int
    ) {

        val db = writableDatabase

        val cursor = db.query(
            "steps",
            arrayOf("id"),
            "recipe_id = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            "step_number ASC"
        )

        db.beginTransaction()

        try {

            var number = 1

            cursor.use {

                while (it.moveToNext()) {

                    val stepId =
                        it.getInt(0)

                    val values =
                        ContentValues().apply {
                            put(
                                "step_number",
                                number
                            )
                        }

                    db.update(
                        "steps",
                        values,
                        "id = ?",
                        arrayOf(stepId.toString())
                    )

                    number++
                }
            }

            db.setTransactionSuccessful()

        } finally {

            db.endTransaction()
        }
    }

    // =========================
    // 調理履歴保存
    // =========================
    fun insertCookingHistory(
        recipeId: Int,
        startTime: String,
        finishTime: String,
        elapsedTime: Int,
        comment: String
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("recipe_id", recipeId)
            put("start_time", startTime)
            put("finish_time", finishTime)
            put("elapsed_time", elapsedTime)
            put("comment", comment)
        }

        return db.insert(
            "cooking_history",
            null,
            values
        )
    }

    // =========================
    // レシピ更新
    // =========================
    fun updateRecipe(
        recipeId: Int,
        name: String,
        description: String,
        cookTime: Int,
        imageUri: String?
    ): Int {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("description", description)
            put("cook_time", cookTime)

            if (imageUri != null) {
                put("image_uri", imageUri)
            } else {
                putNull("image_uri")
            }
        }

        return db.update(
            "recipes",
            values,
            "id = ?",
            arrayOf(recipeId.toString())
        )
    }

    // =========================
    // レシピ削除
    // =========================
    fun deleteRecipe(
        recipeId: Int
    ): Boolean {

        val db = writableDatabase

        db.beginTransaction()

        try {

            // レシピと材料の関係
            db.delete(
                "recipe_ingredients",
                "recipe_id = ?",
                arrayOf(recipeId.toString())
            )

            // レシピとタグの関係
            db.delete(
                "recipe_tags",
                "recipe_id = ?",
                arrayOf(recipeId.toString())
            )

            // 手順
            db.delete(
                "steps",
                "recipe_id = ?",
                arrayOf(recipeId.toString())
            )

            // 調理履歴
            db.delete(
                "cooking_history",
                "recipe_id = ?",
                arrayOf(recipeId.toString())
            )

            // 最後にレシピ本体
            val result =
                db.delete(
                    "recipes",
                    "id = ?",
                    arrayOf(recipeId.toString())
                )

            db.setTransactionSuccessful()

            return result > 0

        } finally {

            db.endTransaction()
        }
    }

    // =========================
    // レシピから材料を削除
    // =========================
    fun deleteIngredientFromRecipe(
        recipeId: Int,
        ingredientId: Int
    ): Int {

        val db = writableDatabase

        return db.delete(
            "recipe_ingredients",
            "recipe_id = ? AND ingredient_id = ?",
            arrayOf(
                recipeId.toString(),
                ingredientId.toString()
            )
        )
    }

    // =========================
    // お気に入り状態変更
    // =========================
    fun updateFavorite(
        recipeId: Int,
        favorite: Boolean
    ): Int {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(
                "favorite",
                if (favorite) 1 else 0
            )
        }

        return db.update(
            "recipes",
            values,
            "id = ?",
            arrayOf(recipeId.toString())
        )
    }

    // =========================
    // お気に入りレシピ一覧
    // =========================
    fun getFavoriteRecipes(): MutableList<Recipe> {

        val recipeList =
            mutableListOf<Recipe>()

        val db =
            readableDatabase

        val cursor =
            db.query(
                "recipes",
                null,
                "favorite = ?",
                arrayOf("1"),
                null,
                null,
                "id DESC"
            )

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(
                        it.getColumnIndexOrThrow("id")
                    )

                val name =
                    it.getString(
                        it.getColumnIndexOrThrow("name")
                    )

                val description =
                    it.getString(
                        it.getColumnIndexOrThrow("description")
                    ) ?: ""

                val cookTime =
                    it.getInt(
                        it.getColumnIndexOrThrow("cook_time")
                    )

                val imageUri =
                    it.getString(
                        it.getColumnIndexOrThrow("image_uri")
                    )

                // タグ取得
                val tags =
                    getTagsByRecipeId(id)
                        .map { tag -> tag.name }

                recipeList.add(
                    Recipe(
                        id = id,
                        name = name,
                        description = description,
                        cookTime = cookTime,
                        imageUri = imageUri,
                        favorite = true,
                        tags = tags
                    )
                )
            }
        }

        return recipeList
    }

    // =========================
    // お気に入り状態取得
    // =========================
    fun isFavorite(
        recipeId: Int
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            "recipes",
            arrayOf("favorite"),
            "id = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                return it.getInt(
                    it.getColumnIndexOrThrow("favorite")
                ) == 1
            }
        }

        return false
    }

    // =========================
    // 調理履歴一覧取得
    // =========================
    fun getAllCookingHistories(): MutableList<CookingHistory> {

        val historyList =
            mutableListOf<CookingHistory>()

        val db = readableDatabase

        val query = """
        SELECT
            cooking_history.id,
            cooking_history.recipe_id,
            recipes.name,
            cooking_history.start_time,
            cooking_history.finish_time,
            cooking_history.elapsed_time,
            cooking_history.comment
        FROM cooking_history
        INNER JOIN recipes
            ON cooking_history.recipe_id = recipes.id
        ORDER BY cooking_history.start_time DESC
    """.trimIndent()

        val cursor =
            db.rawQuery(query, null)

        cursor.use {

            while (it.moveToNext()) {

                val id =
                    it.getInt(0)

                val recipeId =
                    it.getInt(1)

                val recipeName =
                    it.getString(2)

                val startTime =
                    it.getString(3) ?: ""

                val finishTime =
                    it.getString(4) ?: ""

                val elapsedTime =
                    it.getInt(5)

                val comment =
                    it.getString(6) ?: ""

                historyList.add(
                    CookingHistory(
                        id = id,
                        recipeId = recipeId,
                        recipeName = recipeName,
                        startTime = startTime,
                        finishTime = finishTime,
                        elapsedTime = elapsedTime,
                        comment = comment
                    )
                )
            }
        }

        return historyList
    }
}