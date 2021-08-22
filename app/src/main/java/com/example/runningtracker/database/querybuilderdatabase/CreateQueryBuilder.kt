package com.example.runningtracker.database.querybuilderdatabase

import android.database.sqlite.SQLiteDatabase

class CreateQueryBuilder {

    private var tableName = ""
    private val tableFieldsWithParameter = mutableMapOf<String, Any>()

    companion object {
        private const val NOT_NULL = " NOT NULL"
        const val INTEGER = "INTEGER"
        const val LONG = "LONG"
        const val REAL = "REAL"
    }

    fun setTableName(tableName: String): CreateQueryBuilder {
        this.tableName = tableName
        return this
    }

    fun pkField(id: String, autoIncrement: Boolean): CreateQueryBuilder {
        val field = StringBuilder()
        field.append("INTEGER NOT NULL PRIMARY KEY")
        if (!autoIncrement) {
            field.append(" AUTOINCREMENT")
        }
        this.tableFieldsWithParameter[id] = field.toString()
        return this
    }

    fun createIntegerColumn(name: String, isEqualNull: Boolean): CreateQueryBuilder {
        val field = StringBuilder()
        field.append(INTEGER)
        if (!isEqualNull) {
            field.append(NOT_NULL)
        }
        this.tableFieldsWithParameter[name] = field.toString()
        return this
    }

    fun createLongColumn(name: String, isEqualNull: Boolean): CreateQueryBuilder {
        val field = StringBuilder()
        field.append(LONG)
        if (!isEqualNull) {
            field.append(NOT_NULL)
        }
        this.tableFieldsWithParameter[name] = field.toString()
        return this
    }

    fun createRealColumn(name: String, isEqualNull: Boolean): CreateQueryBuilder {
        val field = StringBuilder()
        field.append(REAL)
        if (!isEqualNull) {
            field.append(NOT_NULL)
        }
        this.tableFieldsWithParameter[name] = field.toString()
        return this
    }

    fun create(db: SQLiteDatabase?) {
        val queryForCreateDatabaseTable = mutableListOf<String>()
        tableFieldsWithParameter.forEach {
            queryForCreateDatabaseTable.add("${it.key} ${it.value}")
        }
        db?.execSQL(
            queryForCreateDatabaseTable.joinToString(
                ",",
                "CREATE TABLE $tableName (",
                ")"
            )
        )
    }

}