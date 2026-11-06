package com.example.crave.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.crave.data.model.MealDetailModel
import com.example.crave.data.model.MealModel

/**
 * Local SQLite database: the "cookbook".
 *
 * Stores the FULL recipe so it can be viewed without internet.
 * It only does CRUD operations (data access). It decides nothing: the ViewModel decides,
 * and MealRepository calls these methods inside Dispatchers.IO.
 */
class MealDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "crave.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_COOKBOOK = "cookbook"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_THUMBNAIL = "thumbnail"
        private const val COL_CATEGORY = "category"
        private const val COL_AREA = "area"
        private const val COL_INSTRUCTIONS = "instructions"
        private const val COL_INGREDIENTS = "ingredients"

        // Separator used to store the ingredient list in a single text column.
        private const val INGREDIENTS_SEPARATOR = "\n"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // The id is the same as the API's (it's a String, hence TEXT): no duplicate recipes.
        db.execSQL(
            """
            CREATE TABLE $TABLE_COOKBOOK (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NAME TEXT NOT NULL,
                $COL_THUMBNAIL TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_AREA TEXT NOT NULL,
                $COL_INSTRUCTIONS TEXT NOT NULL,
                $COL_INGREDIENTS TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COOKBOOK")
        onCreate(db)
    }

    /** INSERT. Returns the inserted rowId, or -1 if it failed. */
    fun insertMeal(meal: MealDetailModel): Long {
        val values = ContentValues().apply {
            put(COL_ID, meal.id)
            put(COL_NAME, meal.name)
            put(COL_THUMBNAIL, meal.thumbnail)
            put(COL_CATEGORY, meal.category)
            put(COL_AREA, meal.area)
            put(COL_INSTRUCTIONS, meal.instructions)
            put(COL_INGREDIENTS, meal.ingredients.joinToString(INGREDIENTS_SEPARATOR))
        }
        return writableDatabase.insertWithOnConflict(
            TABLE_COOKBOOK, null, values, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    /** DELETE by id. Returns the number of deleted rows. */
    fun deleteMeal(id: String): Int =
        writableDatabase.delete(TABLE_COOKBOOK, "$COL_ID = ?", arrayOf(id))

    /** SELECT for the cookbook list: only what the card shows, ordered by name. */
    fun getAllMeals(): List<MealModel> {
        val meals = mutableListOf<MealModel>()
        readableDatabase.query(
            TABLE_COOKBOOK, arrayOf(COL_ID, COL_NAME, COL_THUMBNAIL),
            null, null, null, null, "$COL_NAME ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                meals.add(
                    MealModel(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(COL_THUMBNAIL))
                    )
                )
            }
        }
        return meals
    }

    /** SELECT of ONE full recipe by id. Returns null if it isn't saved. */
    fun getMeal(id: String): MealDetailModel? =
        readableDatabase.query(
            TABLE_COOKBOOK, null, "$COL_ID = ?", arrayOf(id), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) toMealDetail(cursor) else null
        }

    /** Converts the cursor's current row into a MealDetailModel. */
    private fun toMealDetail(cursor: Cursor): MealDetailModel =
        MealDetailModel(
            id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
            thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(COL_THUMBNAIL)),
            category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
            area = cursor.getString(cursor.getColumnIndexOrThrow(COL_AREA)),
            instructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)),
            ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS))
                .split(INGREDIENTS_SEPARATOR)
                .filter { it.isNotBlank() }
        )
}