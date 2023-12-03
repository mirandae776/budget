package edu.msoe.budget_app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SpendingDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "spending_database"
        const val DATABASE_VERSION = 1

        // Define your table and column names specific to spending details
        const val TABLE_NAME = "spending_detail"
        const val COLUMN_ID = "id"
        const val COLUMN_AMOUNT_SPENT = "amount_spent"
        const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your spending details table here
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_AMOUNT_SPENT REAL,
                $COLUMN_DATE TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades here
        Log.d("SpendingDatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")

        // Example: Drop the existing table and recreate it (you might want to handle migrations in a real app)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
