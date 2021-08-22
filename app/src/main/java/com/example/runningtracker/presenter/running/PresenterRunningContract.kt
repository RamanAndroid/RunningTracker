package com.example.runningtracker.presenter.running

import com.example.runningtracker.database.entity.Point
import com.example.runningtracker.presenter.MainContract
import com.example.runningtracker.retrofit.pojo.running.CompletedTrackRequest
import com.example.runningtracker.retrofit.pojo.running.CompletedTrackResponse
import java.util.*

interface PresenterRunningContract {

    interface IViewRunning : MainContract.View {
        fun errorResponse(t: Throwable)
        fun setIdForTrack(id: Int)
        fun getIdFromServer(id:Int)
    }

    interface IPresenterRunning : MainContract.Presenter<IViewRunning> {
        fun saveTrackToServer(completedTrackRequest: CompletedTrackRequest)
        fun insertTrackToDatabase(
            idFromServer: Int,
            beginTime: Long,
            calendar: Calendar,
            distance: Int,
            isSend: Int
        )

        fun insertListPointToDatabase(
            trackId: Int,
            listPoint: List<Point>
        )

        fun getLastIdTrack()
    }
}