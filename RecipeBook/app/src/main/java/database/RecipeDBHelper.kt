package com.example.recipebook.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
                    it.getInt(it.getColumnIndexOrThrow("id"))

                val name =
                    it.getString(it.getColumnIndexOrThrow("name"))

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

                recipeList.add(
                    Recipe(
                        id = id,
                        name = name,
                        description = description,
                        cookTime = cookTime,
                        imageUri = imageUri
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

                return Recipe(
                    id = id,
                    name = name,
                    description = description,
                    cookTime = cookTime,
                    imageUri = imageUri
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

                val name =
                    it.getString(0)

                val amount =
                    it.getString(1) ?: ""

                ingredientList.add(
                    RecipeIngredient(
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
}