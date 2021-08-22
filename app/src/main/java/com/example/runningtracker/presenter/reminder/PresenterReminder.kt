package com.example.runningtracker.presenter.reminder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import bolts.Task
import com.example.runningtracker.database.RunningTrackerDatabaseHelper.Companion.ID
import com.example.runningtracker.database.RunningTrackerDatabaseHelper.Companion.REMINDER_COLUMN_HOUR
import com.example.runningtracker.database.RunningTrackerDatabaseHelper.Companion.REMINDER_COLUMN_MINUTE
import com.example.runningtracker.database.RunningTrackerDatabaseHelper.Companion.REMINDER_COLUMN_TIME
import com.example.runningtracker.database.RunningTrackerDatabaseHelper.Companion.REMINDER_TABLE_NAME
import com.example.runningtracker.database.entity.ReminderEntity
import com.example.runningtracker.database.querybuilderdatabase.InsertQueryBuilder
import com.example.runningtracker.database.querybuilderdatabase.SelectQueryBuilder
import com.example.runningtracker.database.querybuilderdatabase.UpdateQueryBuilder
import com.example.runningtracker.presenter.BasePresenter
import com.example.runningtracker.presenter.authorizations.PresenterSignUp

class PresenterReminder(private val db: SQLiteDatabase) :
    BasePresenter<PresenterReminderContract.IViewReminder>(),
    PresenterReminderContract.IPresenterReminder {

    companion object {
        private const val MAX = "max($ID) as $ID"
        const val ERROR_DELETE_DB = "ERROR_DELETE_DB"
        const val ERROR_CHANGE_DB = "ERROR_CHANGE_DB"
    }

    override fun addReminder(reminder: ReminderEntity) {
        getView().showViewLoading()
        Task.callInBackground {
            InsertQueryBuilder()
                .setTableName(name = REMINDER_TABLE_NAME)
                .insertLong(
                    columnName = REMINDER_COLUMN_TIME,
                    value = reminder.date
                ).insertInteger(
                    columnName = REMINDER_COLUMN_HOUR,
                    value = reminder.hours
                ).insertInteger(
                    columnName = REMINDER_COLUMN_MINUTE,
                    value = reminder.minutes
                ).insert(db = db)
        }.continueWith({
            getView().hideViewLoading()
            if (it.isFaulted) {
                getView().errorResponse(Throwable(PresenterSignUp.ERROR))
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    override fun changeReminder(reminder: ReminderEntity) {
        Task.callInBackground {
            UpdateQueryBuilder()
                .setName(tableName = REMINDER_TABLE_NAME)
                .updatesValues(nameColumn = REMINDER_COLUMN_TIME, updateValue = reminder.date)
                .updatesValues(nameColumn = REMINDER_COLUMN_HOUR, updateValue = reminder.hours)
                .updatesValues(nameColumn = REMINDER_COLUMN_MINUTE, updateValue = reminder.minutes)
                .whereIntegerEqual(nameColumn = ID, value = reminder.id)
                .update(db = db)
        }.continueWith({
            if (it.isFaulted) {
                getView().errorResponse(Throwable(ERROR_CHANGE_DB))
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    override fun deleteReminder(id: Int) {
        Task.callInBackground {
            UpdateQueryBuilder()
                .setName(tableName = REMINDER_TABLE_NAME)
                .whereIntegerEqual(nameColumn = ID, id)
                .delete(db = db)
        }.continueWith({
            if (it.isFaulted) {
                getView().errorResponse(Throwable(ERROR_DELETE_DB))
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    override fun getListReminder() {
        var cursor: Cursor? = null
        val listReminder = mutableListOf<ReminderEntity>()
        getView().showViewLoading()
        Task.callInBackground {
            cursor = SelectQueryBuilder()
                .selectColumn(columnName = "*")
                .setTableName(table = REMINDER_TABLE_NAME)
                .select(db = db)
            cursor?.let {
                if (it.moveToFirst()) {
                    val dateIndex = it.getColumnIndexOrThrow(REMINDER_COLUMN_TIME)
                    val idIndex = it.getColumnIndexOrThrow(ID)
                    val hoursIndex = it.getColumnIndexOrThrow(REMINDER_COLUMN_HOUR)
                    val minutesIndex = it.getColumnIndexOrThrow(REMINDER_COLUMN_MINUTE)
                    do {
                        listReminder.add(
                            ReminderEntity(
                                date = it.getString(dateIndex).toLong(),
                                id = it.getInt(idIndex),
                                hours = it.getString(hoursIndex).toInt(),
                                minutes = it.getString(minutesIndex).toInt()
                            )
                        )
                    } while (it.moveToNext())
                }
            }
        }.continueWith({
            cursor?.close()
            getView().hideViewLoading()
            if (it.isFaulted) {
                getView().errorResponse(Throwable(PresenterSignUp.ERROR))
            } else {
                getView().setData(listReminder)
            }
        }, Task.UI_THREAD_EXECUTOR)

    }

    override fun getLastIdReminder() {
        var cursor: Cursor? = null
        var id: Int? = null
        Task.callInBackground {
            cursor = SelectQueryBuilder()
                .setTableName(table = REMINDER_TABLE_NAME)
                .selectColumn(columnName = MAX)
                .select(db = db)
            cursor?.let {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndexOrThrow(ID)
                    do {
                        id = it.getInt(idIndex)
                    } while (it.moveToNext())
                }
            }
        }.continueWith({
            cursor?.close()
            if (id != null) {
                getView().setRequestIdForReminder(id!!)
            }
        }, Task.BACKGROUND_EXECUTOR)
    }


}