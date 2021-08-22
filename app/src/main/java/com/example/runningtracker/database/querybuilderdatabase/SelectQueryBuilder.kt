package com.example.runningtracker.database.querybuilderdatabase

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SelectQueryBuilder {
    private var table: String = ""
    private var whereArgs: String = ""
    private var allParams: MutableList<String> = mutableListOf()

    fun setTableName(table: String): SelectQueryBuilder {
        this.table = table
        return this
    }

    fun where(whereArgs: String): SelectQueryBuilder {
        this.whereArgs = whereArgs
        return this
    }

    fun selectColumn(columnName: String): SelectQueryBuilder {
        this.allParams.add(columnName)
        return this
    }

    fun select(db: SQLiteDatabase): Cursor {
        val allParamsText = allParams.joinToString(",")
        return if (whereArgs == "") {
            db.rawQuery("SELECT $allParamsText FROM $table", null)
        } else {
            db.rawQuery("SELECT $allParamsText FROM $table WHERE $whereArgs", null)
        }
    }

}