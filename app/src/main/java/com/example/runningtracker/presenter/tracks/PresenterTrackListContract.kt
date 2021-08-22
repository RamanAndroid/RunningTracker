package com.example.runningtracker.presenter.tracks

import com.example.runningtracker.database.entity.TrackEntity
import com.example.runningtracker.presenter.MainContract
import com.example.runningtracker.retrofit.pojo.tracks.Track

interface PresenterTrackListContract {

    interface IViewTrackList : MainContract.View {
        fun setData(reminderList: List<TrackEntity>)
        fun errorResponse(t: Throwable)
    }

    interface IPresenterTrackList : MainContract.Presenter<IViewTrackList> {
        fun getTrackFromDatabase(): TrackEntity
        fun getTrackFromServer(): Track
        fun addTrackToServer(track: Track)
    }
}