package com.example.runningtracker.presenter.tracks

import android.database.sqlite.SQLiteDatabase
import com.example.runningtracker.database.entity.TrackEntity
import com.example.runningtracker.presenter.BasePresenter
import com.example.runningtracker.retrofit.pojo.tracks.Track

class PresenterTrackList(private val db: SQLiteDatabase):
    BasePresenter<PresenterTrackListContract.IViewTrackList>()
    ,PresenterTrackListContract.IPresenterTrackList {
    override fun getTrackFromDatabase(): TrackEntity {
        TODO("Not yet implemented")
    }

    override fun getTrackFromServer(): Track {
        TODO("Not yet implemented")
    }

    override fun addTrackToServer(track: Track) {
        TODO("Not yet implemented")
    }
}