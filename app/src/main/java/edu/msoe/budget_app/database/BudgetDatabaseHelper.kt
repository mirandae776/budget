package edu.msoe.budget_app.database
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BudgetDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "budget.db"
        const val TABLE_NAME = "budget_table"
        const val COLUMN_BUDGET = "budget"
    }

    private val SQL_CREATE_ENTRIES = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_BUDGET REAL
        )
    """.trimIndent()

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun getMostRecentBudget(): Int {
        val db = readableDatabase

        val projection = arrayOf(BudgetDatabaseHelper.COLUMN_BUDGET)

        val cursor = db.query(
            BudgetDatabaseHelper.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            "${BudgetDatabaseHelper.COLUMN_BUDGET} DESC", // Order by COLUMN_BUDGET in descending order
            "1" // Limit to 1 result
        )

        var mostRecentBudget = 0

        with(cursor) {
            if (moveToNext()) {
                mostRecentBudget = getInt(getColumnIndexOrThrow(BudgetDatabaseHelper.COLUMN_BUDGET))
            }
            close()
        }

        return mostRecentBudget
    }

}
