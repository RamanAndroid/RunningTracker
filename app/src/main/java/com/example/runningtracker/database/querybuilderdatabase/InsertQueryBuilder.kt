package com.example.runningtracker.database.querybuilderdatabase

import android.database.sqlite.SQLiteDatabase

class InsertQueryBuilder {

    private var tableName: String = ""
    private val selectedFieldsInTable = mutableMapOf<String, String>()

    fun setTableName(name: String): InsertQueryBuilder {
        this.tableName = name
        return this
    }

    fun insertInteger(
        columnName: String,
        value: Int?
    ): InsertQueryBuilder {
        value?.let { selectedFieldsInTable.put(columnName, it.toString()) }
        return this
    }

    fun insertLong(
        columnName: String,
        value: Long?
    ): InsertQueryBuilder {
        value?.let { selectedFieldsInTable.put(columnName, it.toString()) }
        return this
    }

    fun insertDouble(
        columnName: String,
        value: Double?
    ): InsertQueryBuilder {
        value?.let { selectedFieldsInTable.put(columnName, it.toString()) }
        return this
    }

    fun insert(db: SQLiteDatabase) {
        val selectedFields = selectedFieldsInTable.keys.joinToString()
        val questionList = mutableListOf<String>()
        val size = selectedFieldsInTable.size
        while (questionList.size != size) {
            questionList.add("?")
        }
        val stringBuilderForQuestion = questionList.joinToString()
        if (tableName == "" || selectedFieldsInTable.isEmpty()) {
            error("Empty table name or empty selected column")
        } else {
            val statement =
                db.compileStatement("INSERT INTO $tableName ($selectedFields) VALUES ($stringBuilderForQuestion)")
            selectedFieldsInTable.values.forEachIndexed { index, s ->
                statement.bindString(index + 1, s)
            }
            statement.execute()
        }
    }

}