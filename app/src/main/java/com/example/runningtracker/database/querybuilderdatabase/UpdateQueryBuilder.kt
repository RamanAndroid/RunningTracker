package com.example.runningtracker.database.querybuilderdatabase

import android.database.sqlite.SQLiteDatabase

class UpdateQueryBuilder {
    private var tableName = ""
    private val updatesFields = mutableMapOf<String, Any>()
    private var whereArgs = ""
    private var value = ""

    fun setName(tableName: String): UpdateQueryBuilder {
        this.tableName = tableName
        return this
    }

    fun updatesValues(nameColumn: String, updateValue: Int): UpdateQueryBuilder {
        this.updatesFields[nameColumn] = updateValue
        return this
    }

    fun updatesValues(nameColumn: String, updateValue: Long): UpdateQueryBuilder {
        this.updatesFields[nameColumn] = updateValue
        return this
    }

    fun whereIntegerEqual(nameColumn: String, value: Int): UpdateQueryBuilder {
        this.whereArgs = nameColumn
        this.value = value.toString()
        return this
    }

    fun update(db: SQLiteDatabase) {
        val updatingFields = updatesFields.entries.joinToString(",")
        db.compileStatement(
            "UPDATE $tableName SET $updatingFields WHERE $whereArgs"
        ).execute()

    }

    fun delete(db: SQLiteDatabase) {
        if (whereArgs == "") {
            db.execSQL("DELETE FROM $tableName")
        } else {
            db.execSQL("DELETE FROM $tableName WHERE $whereArgs = $value")
        }
    }
}