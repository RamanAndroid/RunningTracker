package com.example.runningtracker.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.runningtracker.database.querybuilderdatabase.CreateQueryBuilder

class RunningTrackerDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null,
    DATABASE_VERSION
) {
    companion object {
        //DATABASE CONFIGURATION
        private const val DATABASE_NAME = "RunningTracker"
        private const val DATABASE_VERSION = 1
        const val ID = "id"

        //REMINDER TABLE CONFIGURE
        const val REMINDER_TABLE_NAME = "reminder"
        const val REMINDER_COLUMN_TIME = "time"
        const val REMINDER_COLUMN_HOUR = "hour"
        const val REMINDER_COLUMN_MINUTE = "minute"

        //TRACKS TABLE CONFIGURE
        const val TRACKS_TABLE_NAME = "tracks"
        const val TRACKS_COLUMN_ID_FROM_SERVER = "id_from_server"
        const val TRACKS_COLUMN_BEGIN_TIME = "begin_time"
        const val TRACKS_COLUMN_RUNNING_TIME = "running_time"
        const val TRACKS_COLUMN_DISTANCE = "distance"
        const val TRACKS_COLUMN_IS_SEND = "is_send"

        //POINTS TABLE CONFIGURE
        const val POINTS_TABLE_NAME = "points"
        const val POINTS_COLUMN_CURRENT_TRACK = "current_track"
        const val POINTS_COLUMN_LATITUDE = "latitude"
        const val POINTS_COLUMN_LONGITUDE = "longitude"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        createReminderTable(db = db)
        createTracksTable(db = db)
        createPointsTable(db = db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    private fun createPointsTable(db: SQLiteDatabase?) {
        CreateQueryBuilder()
            .setTableName(tableName = POINTS_TABLE_NAME)
            .createIntegerColumn(name = ID, isEqualNull = true)
            .createIntegerColumn(name = POINTS_COLUMN_CURRENT_TRACK, isEqualNull = true)
            .createRealColumn(name = POINTS_COLUMN_LATITUDE, isEqualNull = true)
            .createRealColumn(name = POINTS_COLUMN_LONGITUDE, isEqualNull = true)
            .create(db = db)
    }

    private fun createReminderTable(db: SQLiteDatabase?) {
        CreateQueryBuilder()
            .setTableName(tableName = REMINDER_TABLE_NAME)
            .pkField(ID, true)
            .createLongColumn(REMINDER_COLUMN_TIME, true)
            .createIntegerColumn(REMINDER_COLUMN_HOUR, true)
            .createIntegerColumn(REMINDER_COLUMN_MINUTE, true)
            .create(db = db)
    }


    private fun createTracksTable(db: SQLiteDatabase?) {
        CreateQueryBuilder()
            .setTableName(tableName = TRACKS_TABLE_NAME)
            .pkField(ID, true)
            .createIntegerColumn(TRACKS_COLUMN_ID_FROM_SERVER, false)
            .createIntegerColumn(TRACKS_COLUMN_BEGIN_TIME, true)
            .createIntegerColumn(TRACKS_COLUMN_RUNNING_TIME, true)
            .createIntegerColumn(TRACKS_COLUMN_DISTANCE, true)
            .createIntegerColumn(TRACKS_COLUMN_IS_SEND, true)
            .create(db = db)
    }
}