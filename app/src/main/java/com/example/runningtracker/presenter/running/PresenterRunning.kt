package com.example.runningtracker.presenter.running

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import bolts.Task
import com.example.runningtracker.database.RunningTrackerDatabaseHelper
import com.example.runningtracker.database.entity.Point
import com.example.runningtracker.database.querybuilderdatabase.InsertQueryBuilder
import com.example.runningtracker.database.querybuilderdatabase.SelectQueryBuilder
import com.example.runningtracker.presenter.BasePresenter
import com.example.runningtracker.presenter.authorizations.PresenterSignUp
import com.example.runningtracker.retrofit.ApiRunningTracker
import com.example.runningtracker.retrofit.pojo.running.CompletedTrackRequest
import java.util.*

class PresenterRunning(private val db: SQLiteDatabase, private val api: ApiRunningTracker) :
    BasePresenter<PresenterRunningContract.IViewRunning>(),
    PresenterRunningContract.IPresenterRunning {

    companion object {
        const val INVALID_TOKEN = "INVALID_TOKEN"
        const val INVALID_ID = "INVALID_ID"
        const val INVALID_FIELDS = "INVALID_FIELDS"
        const val NO_POINTS = "NO_POINTS"
        const val INVALID_POINTS = "INVALID_POINTS"
        const val MAX =
            "max(${RunningTrackerDatabaseHelper.ID}) as ${RunningTrackerDatabaseHelper.ID}"
        const val ERROR = "ERROR"
    }

    override fun saveTrackToServer(completedTrackRequest: CompletedTrackRequest) {
        var id: Int? = null
        Task.callInBackground {
            return@callInBackground api.saveTrack(completedTrackRequest = completedTrackRequest)
                .execute()
        }.onSuccess({
            when {
                it.result.body()!!.error == INVALID_TOKEN -> {
                    getView().errorResponse(Throwable(INVALID_TOKEN))
                }
                it.result.body()!!.error == INVALID_ID -> {
                    getView().errorResponse(Throwable(INVALID_ID))
                }
                it.result.body()!!.error == INVALID_FIELDS -> {
                    getView().errorResponse(Throwable(INVALID_FIELDS))
                }
                it.result.body()!!.error == NO_POINTS -> {
                    getView().errorResponse(Throwable(NO_POINTS))
                }
                it.result.body()!!.error == INVALID_POINTS -> {
                    getView().errorResponse(Throwable(INVALID_POINTS))
                }
                it.result.body() == null -> {
                    getView().errorResponse(Throwable(PresenterSignUp.ERROR))
                }
                else -> {
                    id = it.result.body()!!.serverId
                }
            }
        }, Task.BACKGROUND_EXECUTOR)
            .continueWith({
                if (it.isFaulted || id == null) {
                    getView().errorResponse(Throwable(ERROR))
                } else {
                    getView().getIdFromServer(id!!)
                }
            }, Task.UI_THREAD_EXECUTOR)

    }

    override fun insertTrackToDatabase(
        idFromServer: Int,
        beginTime: Long,
        calendar: Calendar,
        distance: Int,
        isSend: Int
    ) {
        Task.callInBackground {
            InsertQueryBuilder()
                .setTableName(name = RunningTrackerDatabaseHelper.TRACKS_TABLE_NAME)
                .insertInteger(
                    columnName = RunningTrackerDatabaseHelper.TRACKS_COLUMN_ID_FROM_SERVER,
                    value = idFromServer
                )
                .insertLong(
                    columnName = RunningTrackerDatabaseHelper.TRACKS_COLUMN_BEGIN_TIME,
                    value = beginTime
                )
                .insertLong(
                    columnName = RunningTrackerDatabaseHelper.TRACKS_COLUMN_BEGIN_TIME,
                    value = calendar.time.time
                )
                .insertInteger(
                    columnName = RunningTrackerDatabaseHelper.TRACKS_COLUMN_DISTANCE,
                    value = distance
                )
                .insertInteger(
                    columnName = RunningTrackerDatabaseHelper.TRACKS_COLUMN_IS_SEND,
                    value = isSend
                )
                .insert(db = db)
        }.continueWith({
            if (it.isFaulted) {
                getView().errorResponse(Throwable(PresenterSignUp.ERROR))
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    override fun insertListPointToDatabase(
        trackId: Int,
        listPoint: List<Point>
    ) {
        listPoint.forEach {
            InsertQueryBuilder()
                .setTableName(name = RunningTrackerDatabaseHelper.POINTS_TABLE_NAME)
                .insertInteger(
                    columnName = RunningTrackerDatabaseHelper.POINTS_COLUMN_CURRENT_TRACK,
                    value = trackId
                )
                .insertDouble(
                    columnName = RunningTrackerDatabaseHelper.POINTS_COLUMN_LATITUDE,
                    value = it.lat
                )
                .insertDouble(
                    columnName = RunningTrackerDatabaseHelper.POINTS_COLUMN_LONGITUDE,
                    value = it.lng
                )
                .insert(db = db)
        }
    }

    override fun getLastIdTrack() {
        var cursor: Cursor? = null
        var id: Int? = null
        Task.callInBackground {
            cursor = SelectQueryBuilder()
                .setTableName(table = RunningTrackerDatabaseHelper.REMINDER_TABLE_NAME)
                .selectColumn(columnName = MAX)
                .select(db = db)
            cursor?.let {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndexOrThrow(RunningTrackerDatabaseHelper.ID)
                    do {
                        id = it.getInt(idIndex)
                    } while (it.moveToNext())
                }
            }
        }.continueWith({
            cursor?.close()
            if (id != null) {
                getView().setIdForTrack(id!!)
            }
        }, Task.BACKGROUND_EXECUTOR)
    }

}